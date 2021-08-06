package jetbrains.buildServer.terraformSupportPlugin

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.intellij.execution.process.ProcessOutput
import jetbrains.buildServer.BuildProblemData
import jetbrains.buildServer.agent.*
import jetbrains.buildServer.agent.artifacts.ArtifactsWatcher
import jetbrains.buildServer.messages.serviceMessages.ServiceMessage
import jetbrains.buildServer.messages.serviceMessages.ServiceMessageTypes
import jetbrains.buildServer.terraformSupportPlugin.cmd.*
import jetbrains.buildServer.terraformSupportPlugin.cmd.tf.InitCommand
import jetbrains.buildServer.terraformSupportPlugin.cmd.tf.ShowCommand
import jetbrains.buildServer.terraformSupportPlugin.cmd.tfenv.TfEnvInstallCommand
import jetbrains.buildServer.terraformSupportPlugin.cmd.tfenv.TfEnvUseCommand
import jetbrains.buildServer.terraformSupportPlugin.loggedCommands.LoggedTerraformCommand
import jetbrains.buildServer.terraformSupportPlugin.loggedCommands.LoggedTerraformCommandsParser
import jetbrains.buildServer.terraformSupportPlugin.parsing.PlanData
import jetbrains.buildServer.terraformSupportPlugin.report.TerraformReportGenerator
import jetbrains.buildServer.util.EventDispatcher
import java.io.Closeable
import java.io.File
import java.io.FileWriter
import java.lang.Exception
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
            TerraformRuntimeConstants.ENV_TF_LOG_LEVEL
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

    private fun detectTerraformCommands(logger: BuildProgressLogger): List<LoggedTerraformCommand> {
        val commandsParser = LoggedTerraformCommandsParser(myTerraformLogFile!!, logger)
        val commands = commandsParser.getCommands()
        for (command in commands) {
            logger.debug("Detected Terraform command: '${command.getCommand()}' with arguments ${command.getArguments()}")
            if (command.producedFile()) {
                logger.debug("Detected Terraform plan result file: ${command.getProducedFileName()}")
            }
        }

        return commands
    }

    private fun findPlanOutputFiles(
        build: AgentRunningBuild,
        logger: BuildProgressLogger
    ): List<File> {
        val commands = detectTerraformCommands(logger)
        val rootPath = build.checkoutDirectory.absoluteFile
        val rootPathWalk = rootPath.walk().maxDepth(TerraformRuntimeConstants.OUT_FILE_TREE_WALK_DEPTH)
        val foundFiles = mutableListOf<File>()

        if (commands.any { it.producedFile() }) {
            commands
                .filter { it.producedFile() }
                .forEach { command ->
                    val found = rootPathWalk.filter { it.name == command.getProducedFileName() }.toList()
                    when {
                        found.size > 1 -> {
                            logger.debug("Found multiple files with '${command.getProducedFileName()}' name, will iterate over them")
                        }
                        found.isEmpty() -> {
                            logger.debug("File '${command.getProducedFileName()}' is missing even though it is reported in the Terraform log")
                        }
                        else -> {
                            logger.debug("Found ${command.getProducedFileName()} file")
                        }
                    }
                    foundFiles.addAll(found)
                }
        }

        return foundFiles
    }

    private fun parsePlanDataFromFile(
        runningBuild: AgentRunningBuild,
        logger: BuildProgressLogger,
        configuration: TerraformFeatureConfiguration,
        planOutputFile: File
    ): PlanData? {
        val showCommand = ShowCommand(runningBuild, logger, configuration, planOutputFile)
        val showCommandOutput = showCommand.execute()

        return if (showCommandOutput.exitCode == 0) {
            Gson().fromJson(showCommandOutput.stdout, PlanData::class.java)
        } else {
            logger.warning("'terraform show' failed for the ${planOutputFile.name}")
            null
        }
    }

    private fun generateReport(
        logger: BuildProgressLogger,
        reportPath: String,
        showOutputs: List<JsonObject>
    ) {
        logger.debug("Generating report: ${showOutputs.size} plans detected")

        val gson = GsonBuilder().setPrettyPrinting().create()
        val reportFile = File(reportPath)

        for (output in showOutputs) {
            reportFile.appendText(
                gson.toJson(output)
            )
        }
    }

    override fun sourcesUpdated(runningBuild: AgentRunningBuild) {
        if (isFeatureEnabled(runningBuild)) {
            val logger = getBuildLogger(runningBuild)
            try {
                prepareEnvironment(runningBuild, logger)
            } catch (e: Exception) {
                logger.warning(e.stackTraceToString())
                throw e
            }
        }
    }

    private fun prepareEnvironment(runningBuild: AgentRunningBuild, logger: BuildProgressLogger) {
        val configuration = getFeatureConfiguration(runningBuild)

        if (configuration.useTfEnv()) { // run `tfenv install/use`
            ServiceMessageBlock(logger, "[tfenv] Fetch Terraform").use {
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

    override fun beforeBuildFinish(runningBuild: AgentRunningBuild, buildStatus: BuildFinishedStatus) {
        if (!buildStatus.isFailed && isFeatureEnabled(runningBuild)) {
            val logger = getBuildLogger(runningBuild)
            try {
                handleTerraformOutput(runningBuild, logger)
            } catch (e: Exception) {
                logger.warning(e.stackTraceToString())
                throw e
            }
        }
    }

    private fun handleTerraformOutput(runningBuild: AgentRunningBuild, logger: BuildProgressLogger) {
        if (isReportEnabled(runningBuild)) { // generate temporary report path
            val configuration = getFeatureConfiguration(runningBuild)
            val planOutputFiles = findPlanOutputFiles(runningBuild, logger)

            if (planOutputFiles.isNotEmpty()) {
                ServiceMessageBlock(logger, "Handle Terraform output").use {
                    val planDataList = mutableListOf<PlanData>()

                    planOutputFiles.forEach { file ->
                        myWatcher.addNewArtifactsPath( // publish plan output file(s) as hidden artifacts
                            "${file.absolutePath} => ${TerraformFeatureConstants.HIDDEN_ARTIFACT_REPORT_FOLDER}"
                        )

                        val showOutput = parsePlanDataFromFile(runningBuild, logger, configuration, file)
                        if (showOutput != null) {
                            planDataList.add(showOutput)
                        }
                    }

                    if (planDataList.isNotEmpty()) {
                        logger.debug("Generating report")
                        val reportPath = TerraformReportGenerator(runningBuild, logger, planDataList).generate()

                        myWatcher.addNewArtifactsPath( // save report as hidden artifact
                            "$reportPath => ${TerraformFeatureConstants.HIDDEN_ARTIFACT_REPORT_FOLDER}"
                        )
                    }
                }
            }

            if (!myTerraformLogFile.isNullOrEmpty()) {
                myWatcher.addNewArtifactsPath( // save Terraform log as hidden artifact
                    "$myTerraformLogFile => ${TerraformFeatureConstants.HIDDEN_ARTIFACT_REPORT_FOLDER}"
                )
            }
        }
    }

    companion object {
        class ServiceMessageBlock(
            private val myLogger: BuildProgressLogger,
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