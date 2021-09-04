package jetbrains.buildServer.terraformSupportPlugin

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import jetbrains.buildServer.BuildProblemData
import jetbrains.buildServer.agent.*
import jetbrains.buildServer.agent.artifacts.ArtifactsWatcher
import jetbrains.buildServer.messages.serviceMessages.ServiceMessage
import jetbrains.buildServer.messages.serviceMessages.ServiceMessageTypes
import jetbrains.buildServer.terraformSupportPlugin.parsing.PlanData
import jetbrains.buildServer.terraformSupportPlugin.parsing.ResourceChange
import jetbrains.buildServer.terraformSupportPlugin.report.TerraformReportGenerator
import jetbrains.buildServer.util.EventDispatcher
import java.io.Closeable
import java.io.File
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

    private fun getObjectMapper(): ObjectMapper {
        return ObjectMapper()
            .registerModule(KotlinModule(nullIsSameAsDefault = true))
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
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
        getObjectMapper()
            .writerWithDefaultPrettyPrinter()
            .writeValue(
                varFile,
                formatSystemProperties(build)
            )

        return varFile.absolutePath
    }

    private fun parsePlanDataFromFile(
        logger: BuildProgressLogger,
        planOutputFile: File
    ): PlanData {
        logger.debug("Parsing report data from the ${planOutputFile.absolutePath}")
        val planData = getObjectMapper().readValue(
            planOutputFile,
            PlanData::class.java
        )
        planData.fileName = planOutputFile.name
        return planData
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

        if (configuration.exportSystemProperties()) { // generate temporary file in defined path containing system vars in Terraform format
            val systemPropertiesFilePath = saveSystemPropertiesToFile(
                runningBuild,
                configuration.systemPropertiesOutFile()!!
            )
            logger.debug("Saved system properties at the '${systemPropertiesFilePath}' path")
        }
    }

    private fun logResourceTypeData(
        logger: BuildProgressLogger,
        resource: ResourceChange,
        pattern: String,
        matches: Boolean
    ) {
        logger.debug("-=- Checking resource type ${resource.type} -=-")
        logger.debug("isChanged: ${resource.changeItem.isChanged}, " +
                "isDeleted: ${resource.changeItem.isDeleted}, " +
                "isReplaced: ${resource.changeItem.isReplaced}")
        logger.debug("matches pattern ${pattern}: $matches")
        logger.debug("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-")
    }

    private fun checkProtectedResources(
        logger: BuildProgressLogger,
        configuration: TerraformFeatureConfiguration,
        planData: PlanData
    ): Boolean {
        logger.message("Handling protected resources")
        val changedProtectedResources = planData
            .changedResources
            .filter {
                val pattern = configuration.getProtectedResourcePattern()
                val matches = pattern.matches(it.type)
                logResourceTypeData(logger, it, pattern.toString(), matches)

                matches && (it.changeItem.isDeleted || it.changeItem.isReplaced)
            }
        changedProtectedResources.forEach {
            createBuildProblem(
                logger,
                "Protected resource ${it.address} is planned for destroy or replace",
                "Protected resource '${it.type}' is planned for destroy or replace"
            )
        }

        return changedProtectedResources.isNotEmpty()
    }

    private fun updateBuildStatusWithPlanData(
        logger: BuildProgressLogger,
        planData: PlanData,
        plannedProtectedResourceChanges: Boolean
    ) {
        logger.message("Updating build status")
        if (!planData.hasChangedResources) {
            updateBuildStatus(logger, "No resource changes are planned")
        } else if (plannedProtectedResourceChanges) {
            updateBuildStatus(logger, "Protected resources are planned for replacement/destroy, check the report")
        } else if (planData.hasChangedResources && planData.changedResources.any { it.changeItem.isDeleted || it.changeItem.isReplaced }) {
            updateBuildStatus(logger, "Some of resources are planned for replacement/destroy, check the report")
        } else {
            updateBuildStatus(logger, "Resource changes are planned")
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
                configuration.getPlanJsonFile()!!
            )
            val reportFile = File(
                runningBuild.agentTempDirectory,
                TerraformFeatureConstants.HIDDEN_ARTIFACT_REPORT_FILENAME
            )

            ServiceMessageBlock(logger, "Handle Terraform output").use {
                val planData = parsePlanDataFromFile(logger, planFile)
                TerraformReportGenerator(runningBuild, logger, planData).generate(reportFile)

                var plannedProtectedResourceChanges: Boolean = false
                if (configuration.hasProtectedResourcePattern()) {
                    plannedProtectedResourceChanges = checkProtectedResources(logger, configuration, planData)
                }

                if (configuration.updateBuildStatus()) {
                    updateBuildStatusWithPlanData(logger, planData, plannedProtectedResourceChanges)
                }
            }

            myWatcher.addNewArtifactsPath( // publish report and plan file as hidden artifacts
                buildString {
                    appendLine("${planFile.absolutePath} => ${TerraformFeatureConstants.HIDDEN_ARTIFACT_REPORT_FOLDER}")
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

        fun createBuildProblem(
            logger: BuildProgressLogger,
            problemUniqueDescription: String,
            problemGenericDescription: String
        ) {
            val problemIdentityHash = problemUniqueDescription.hashCode()

            logger.logBuildProblem(
                BuildProblemData.createBuildProblem(
                    problemIdentityHash.toString(),
                    "PlannedChangesProblem",
                    problemGenericDescription
                )
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