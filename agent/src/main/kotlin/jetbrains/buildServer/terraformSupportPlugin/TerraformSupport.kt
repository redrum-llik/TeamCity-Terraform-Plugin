package jetbrains.buildServer.terraformSupportPlugin

import com.google.gson.Gson
import com.intellij.execution.process.ProcessOutput
import jetbrains.buildServer.BuildProblemData
import jetbrains.buildServer.agent.*
import jetbrains.buildServer.agent.artifacts.ArtifactsWatcher
import jetbrains.buildServer.messages.serviceMessages.ServiceMessage
import jetbrains.buildServer.messages.serviceMessages.ServiceMessageTypes
import jetbrains.buildServer.terraformSupportPlugin.cmd.*
import jetbrains.buildServer.terraformSupportPlugin.cmd.tfenv.TfEnvInstallCommand
import jetbrains.buildServer.terraformSupportPlugin.cmd.tfenv.TfEnvUseCommand
import jetbrains.buildServer.util.EventDispatcher
import java.io.Closeable
import java.io.File
import java.io.FileWriter
import java.util.*

class TerraformSupport(
    events: EventDispatcher<AgentLifeCycleListener>,
    watcher: ArtifactsWatcher
) : AgentLifeCycleAdapter() {

    private val myWatcher = watcher
    private var myTerraformLogFile: String? = null
    private val myFlowId = FlowGenerator.generateNewFlow()

    init {
        events.addListener(this)
    }

    private fun isFeatureEnabled(build: AgentRunningBuild): Boolean {
        return getFeature(build) != null
    }

    private fun getFeature(build: AgentRunningBuild): AgentBuildFeature? {
        val features = build.getBuildFeaturesOfType(TerraformFeatureConstants.FEATURE_TYPE)
        if (features.isNotEmpty()) {
            return features.first()
        }
        return null
    }

    private fun getFeatureConfiguration(build: AgentRunningBuild): TerraformFeatureConfiguration {
        return TerraformFeatureConfiguration(
            getFeature(build)!!.parameters
        )
    }

    private fun getBuildLogger(build: AgentRunningBuild): FlowLogger {
        return build.buildLogger.getFlowLogger(myFlowId)!!
    }

    private fun formatSystemProperties(build: AgentRunningBuild): MutableMap<String, String> {
        val result: MutableMap<String, String> = HashMap()
        val systemProperties = build.sharedBuildParameters.systemProperties

        for (parameter in systemProperties) {
            // Terraform variables cannot include dots
            val newKey = parameter.key.replace(".", "_")
            result[newKey] = parameter.value
        }
        return result
    }

    private fun saveSystemPropertiesToFile(build: AgentRunningBuild, filePath: String): String {
        val varFile = File(
            build.checkoutDirectory,
            filePath
        ).normalize()
        val writer = FileWriter(varFile)
        val json = Gson().toJson(
            formatSystemProperties(build)
        )
        writer.run {
            write(json)
            close()
        }

        return varFile.absolutePath
    }

    private fun passEnvParamToBuild(
        build: AgentRunningBuild,
        logger: BuildProgressLogger,
        name: String,
        value: String = String()
    ) {
        if (build.sharedBuildParameters.environmentVariables.containsKey(name)) {
            logger.warning("Overriding environment variable $name with value $value")
        } else {
            logger.debug("Adding environment variable $name: $value")
        }
        build.addSharedEnvironmentVariable(name, value)
    }

    private fun isReportEnabled(runningBuild: AgentRunningBuild): Boolean {
        val param =
            runningBuild.sharedBuildParameters.allParameters[TerraformFeatureConstants.BUILD_PARAM_REPORT_ENABLED]
        if (param == null || param.toBoolean()) {
            return true
        }
        return false
    }

    private fun passForceTerraformLogFile(
        build: AgentRunningBuild,
        logger: BuildProgressLogger
    ) {
        logger.debug("Forcing extra logging for Terraform command executions")
        passEnvParamToBuild(
            build,
            logger,
            TerraformRuntimeConstants.ENV_TF_LOG,
            TerraformRuntimeConstants.ENV_TF_LOG_INFO
        )

        // generate a temporary file in the agent temp directory to store the logs produced by Terraform executions
        myTerraformLogFile = File(
            build.agentTempDirectory.absolutePath,
            "terraform_log_${UUID.randomUUID()}.txt"
        ).absolutePath
        logger.debug("Terraform log file path: $myTerraformLogFile")
        passEnvParamToBuild(
            build,
            logger,
            TerraformRuntimeConstants.ENV_TF_LOG_PATH,
            myTerraformLogFile!!
        )
    }

    private fun runShow(runningBuild: AgentRunningBuild) {
        // need to a) check if the terraform > 12 (maybe show a warn if the version is older) and b) capture the stdout output as JSON
        TODO("Not yet implemented")
    }

    private fun generateReport(
        runningBuild: AgentRunningBuild,
        logger: FlowLogger,
        reportPath: String
    ) {
        TODO("Not yet implemented")
    }

    override fun sourcesUpdated(runningBuild: AgentRunningBuild) {
        if (isFeatureEnabled(runningBuild)) {
            val logger = getBuildLogger(runningBuild)
            val configuration = getFeatureConfiguration(runningBuild)

            if (configuration.runInitializationStage()) {
                ServiceMessageBlock(logger, "Terraform initialization").use {
                    if (configuration.useTfEnv()) { // run `tfenv install/use`
                        ServiceMessageBlock(logger, "Fetch Terraform").use {
                            val installCommand = TfEnvInstallCommand(runningBuild, logger, configuration)
                            val installCommandOutput = installCommand.execute()

                            if (installCommandOutput.exitCode != 0) {
                                createBuildProblem(installCommand, installCommandOutput)
                            }

                            val useCommand = TfEnvUseCommand(runningBuild, logger, configuration)
                            val useCommandOutput = useCommand.execute()

                            if (useCommandOutput.exitCode != 0) {
                                createBuildProblem(useCommand, useCommandOutput)
                            }
                        }
                    }

                    if (configuration.useWorkspace()) { // try to switch to specified workspace
                        ServiceMessageBlock(logger, "Switch to workspace").use {
                            val selectCommand = WorkspaceSelectCommand(runningBuild, logger, configuration)
                            val selectCommandOutput = selectCommand.execute()

                            if (selectCommandOutput.exitCode != 0) {
                                val noWorkspaceFound = WorkspaceSelectCommand.checkIfNoWorkspaceFoundErrorInOutput(selectCommandOutput)

                                if (noWorkspaceFound && configuration.createWorkspaceIfNotFound()) {
                                    val newCommand = WorkspaceNewCommand(runningBuild, logger, configuration)
                                    val newCommandOutput = newCommand.execute()

                                    if (newCommandOutput.exitCode != 0) {
                                        createBuildProblem(newCommand, newCommandOutput)
                                    }
                                } else if (noWorkspaceFound) {
                                    createBuildProblem(selectCommandOutput, "No workspace found")
                                } else {
                                    createBuildProblem(selectCommand, selectCommandOutput)
                                }
                            }
                        }
                    }

                    if (configuration.doInit()) { // run 'terraform init'
                        ServiceMessageBlock(logger, "Do init").use {
                            val initCommand = InitCommand(runningBuild, logger, configuration)
                            val output = initCommand.execute()
                            if (output.exitCode != 0) {
                                createBuildProblem(initCommand, output)
                            }
                        }
                    }
                }
            }

            if (isReportEnabled(runningBuild)) { // force Terraform to log executions into a file
                passForceTerraformLogFile(runningBuild, logger)
            }

            if (configuration.exportSystemProperties()) { // generate temporary file in defined path containing system vars in Ansible format
                val systemPropertiesFilePath = saveSystemPropertiesToFile(
                    runningBuild,
                    configuration.systemPropertiesOutFile()!!
                )
                logger.debug("Saved system properties at the '${systemPropertiesFilePath}' path")
            }
        }
    }

    override fun beforeBuildFinish(runningBuild: AgentRunningBuild, buildStatus: BuildFinishedStatus) {
        if (isFeatureEnabled(runningBuild)) {
            if (isReportEnabled(runningBuild)) { // generate temporary report path
                val reportPath = File(
                    runningBuild.agentTempDirectory,
                    TerraformFeatureConstants.HIDDEN_ARTIFACT_REPORT_FILENAME
                ).absolutePath
                val logger = getBuildLogger(runningBuild)

                generateReport(
                    runningBuild,
                    logger,
                    reportPath
                )

                myWatcher.addNewArtifactsPath( // save report as hidden artifact
                    "$reportPath => ${TerraformFeatureConstants.HIDDEN_ARTIFACT_REPORT_FOLDER}"
                )

                myWatcher.addNewArtifactsPath( // save Terraform log as hidden artifact
                    "$myTerraformLogFile => ${TerraformFeatureConstants.HIDDEN_ARTIFACT_REPORT_FOLDER}"
                )
            }
        }
    }

    companion object {
        class ServiceMessageBlock(
            private val myLogger: FlowLogger,
            private val myBlockName: String
        ) : Closeable {
            init {
                val serviceMessage = ServiceMessage.asString(
                    ServiceMessageTypes.BLOCK_OPENED,
                    mapOf("name" to myBlockName)
                )
                myLogger.message(serviceMessage)
            }

            override fun close() {
                val serviceMessage = ServiceMessage.asString(
                    ServiceMessageTypes.BLOCK_CLOSED,
                    mapOf("name" to myBlockName)
                )
                myLogger.message(serviceMessage)
            }
        }

        fun createBuildProblem(command: BaseCommand, output: ProcessOutput) {
            createBuildProblem(output, "${command.describe()} failed")
        }

        fun createBuildProblem(output: ProcessOutput, problemText: String) {
            val problemIdentityHash = output.stderr.hashCode()

            BuildProblemData.createBuildProblem(
                problemIdentityHash.toString(),
                "TerraformExecutionProblem",
                problemText
            )
        }
    }
}