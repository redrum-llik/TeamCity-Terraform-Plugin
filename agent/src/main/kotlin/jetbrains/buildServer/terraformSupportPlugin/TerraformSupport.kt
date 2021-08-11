package jetbrains.buildServer.terraformSupportPlugin

import com.google.gson.Gson
import com.intellij.execution.process.ProcessOutput
import jetbrains.buildServer.BuildProblemData
import jetbrains.buildServer.agent.*
import jetbrains.buildServer.agent.artifacts.ArtifactsWatcher
import jetbrains.buildServer.messages.serviceMessages.ServiceMessage
import jetbrains.buildServer.messages.serviceMessages.ServiceMessageTypes
import jetbrains.buildServer.terraformSupportPlugin.cmd.BaseCommand
import jetbrains.buildServer.terraformSupportPlugin.cmd.tf.ShowCommand
import jetbrains.buildServer.terraformSupportPlugin.cmd.tfenv.TfEnvInstallCommand
import jetbrains.buildServer.terraformSupportPlugin.cmd.tfenv.TfEnvUseCommand
import jetbrains.buildServer.terraformSupportPlugin.parsing.PlanData
import jetbrains.buildServer.terraformSupportPlugin.report.TerraformReportGenerator
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

    private fun parsePlanDataFromFile(
        runningBuild: AgentRunningBuild,
        logger: BuildProgressLogger,
        configuration: TerraformFeatureConfiguration,
        planOutputFile: File
    ): PlanData? {
        val showCommand = ShowCommand(runningBuild, logger, configuration, planOutputFile)
        val showCommandOutput = showCommand.execute()

        return if (showCommandOutput.exitCode == 0) {
            val planData = Gson().fromJson(showCommandOutput.stdout, PlanData::class.java)
            planData.fileName = planOutputFile.name
            planData
        } else {
            logger.warning("'terraform show' failed for the ${planOutputFile.name}")
            null
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

        if (configuration.exportSystemProperties()) { // generate temporary file in defined path containing system vars in Terraform format
            val systemPropertiesFilePath = saveSystemPropertiesToFile(
                runningBuild,
                configuration.systemPropertiesOutFile()!!
            )
            logger.debug("Saved system properties at the '${systemPropertiesFilePath}' path")
        }
    }

    private fun checkProtectedResources(configuration: TerraformFeatureConfiguration, planData: PlanData?) {
        if (planData != null) {
            val changedProtectedResources = planData
                .changedResources
                .filter {
                    it.type in configuration.getProtectedResources() &&
                            (it.changeItem.isDeleted || it.changeItem.isReplaced)
                }
            changedProtectedResources.forEach {
                createBuildProblem(
                    "Protected resource ${it.name} is planned for destroy or replace",
                    "Protected resource change detected"
                )
            }
        }
    }

    private fun updateBuildStatusWithPlanData(logger: BuildProgressLogger, planData: PlanData?) {
        if (planData != null) {
            if (!planData.hasChangedResources) {
                updateBuildStatus(logger, "No resource changes are planned")
            } else if (planData.hasChangedResources && planData.changedResources.any { it.changeItem.isDeleted || it.changeItem.isReplaced }) {
                updateBuildStatus(logger, "Some of resources are planned for replacement/destroy, check the report")
            } else {
                updateBuildStatus(logger, "Resource changes are planned")
            }
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
        val configuration = getFeatureConfiguration(runningBuild)
        if (configuration.isReportEnabled()) { // generate temporary report path
            val planFile = File(
                runningBuild.checkoutDirectory,
                configuration.getPlanFile()!!
            )
            val reportFile = File(
                runningBuild.agentTempDirectory,
                TerraformFeatureConstants.HIDDEN_ARTIFACT_REPORT_FILENAME
            )

            ServiceMessageBlock(logger, "Handle Terraform output").use {
                val planData = parsePlanDataFromFile(runningBuild, logger, configuration, planFile)
                if (planData != null) {
                    TerraformReportGenerator(runningBuild, logger, planData).generate(reportFile)
                } else {
                    logger.warning("Failed to parse plan data out of ${planFile.absolutePath}")
                }

                if (configuration.hasProtectedResources()) {
                    checkProtectedResources(configuration, planData)
                }

                if (configuration.updateBuildStatus()) {
                    updateBuildStatusWithPlanData(logger, planData)
                }
            }

            myWatcher.addNewArtifactsPath( // publish report and plan file as hidden artifacts
                buildString {
                    appendLine("${planFile.absolutePath} => ${TerraformFeatureConstants.HIDDEN_ARTIFACT_REPORT_FOLDER}/${TerraformFeatureConstants.HIDDEN_ARTIFACT_PLAN_FILENAME}")
                    appendLine("${reportFile.absolutePath} => ${TerraformFeatureConstants.HIDDEN_ARTIFACT_REPORT_FOLDER}")
                }
            )
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
            createBuildProblem(output.stderr, "${command.describe()} failed")
        }

        fun createBuildProblem(problemUniqueDescription: String, problemGenericDescription: String) {
            val problemIdentityHash = problemUniqueDescription.hashCode()

            BuildProblemData.createBuildProblem(
                problemIdentityHash.toString(),
                "TerraformExecutionProblem",
                problemGenericDescription
            )
        }

        fun updateBuildStatus(
            logger: BuildProgressLogger,
            statusText: String,
            includeCalculatedPrefix: Boolean = false
        ) {
            val arguments = mutableMapOf<String, String>()
            when {
                includeCalculatedPrefix -> {
                    arguments["text"] = "{build.status.text}$statusText"
                }
                else -> {
                    arguments["text"] = statusText
                }
            }

            logger.message(
                ServiceMessage.asString(
                    ServiceMessageTypes.BUILD_STATUS,
                    arguments
                )
            )
        }
    }
}