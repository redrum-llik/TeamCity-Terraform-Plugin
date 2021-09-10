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
import jetbrains.buildServer.util.regex.MatcherUtil
import java.io.Closeable
import java.io.File

class TerraformSupport(
    events: EventDispatcher<AgentLifeCycleListener>,
    watcher: ArtifactsWatcher
) : AgentLifeCycleAdapter() {

    private val myWatcher = watcher
    private val myFlowId = FlowGenerator.generateNewFlow()

    init {
        events.addListener(this)
    }

    private fun getFeature(build: AgentRunningBuild): AgentBuildFeature? {
        val features = build.getBuildFeaturesOfType(TerraformFeatureConstants.FEATURE_TYPE)
        if (features.isNotEmpty()) {
            return features.first() // isMultipleFeaturesPerBuildTypeAllowed = false on server side
        }
        return null
    }

    private fun getFeatureConfiguration(feature: AgentBuildFeature): TerraformFeatureConfiguration {
        return TerraformFeatureConfiguration(
            feature.parameters
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

    private fun logResourceTypeData(
        logger: BuildProgressLogger,
        resource: ResourceChange,
        matches: Boolean
    ) {
        logger.debug("-=- Checking resource type ${resource.type} -=-")
        logger.debug("isChanged: ${resource.changeItem.isChanged}, " +
                "isDeleted: ${resource.changeItem.isDeleted}, " +
                "isReplaced: ${resource.changeItem.isReplaced}")
        logger.debug("matches: $matches")
        logger.debug("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-")
    }

    private fun checkProtectedResources(
        logger: BuildProgressLogger,
        configuration: TerraformFeatureConfiguration,
        planData: PlanData
    ): Boolean {
        val pattern = configuration.getProtectedResourcePattern()!!
        logger.message("Handling protected resources (pattern: '$pattern')")

        val changedProtectedResources = planData
            .changedResources
            .filter {
                val matches = MatcherUtil.matches(it.type, pattern, 10)
                logResourceTypeData(logger, it, matches)

                matches && (it.changeItem.isDeleted || it.changeItem.isReplaced)
            }
        changedProtectedResources.forEach {
            createBuildProblem(
                logger,
                "Protected resource '${it.address}' is planned for destroy or replace",
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
        val feature = getFeature(runningBuild)
        if (!buildStatus.isFailed && feature != null) {
            val logger = getBuildLogger(runningBuild)
            try {
                handleTerraformOutput(runningBuild, feature, logger)
            } catch (e: Exception) {
                logger.warning(e.stackTraceToString())
                throw e
            }
        }
    }

    private fun handleTerraformOutput(
        runningBuild: AgentRunningBuild,
        feature: AgentBuildFeature,
        logger: BuildProgressLogger
    ) {
        val configuration = getFeatureConfiguration(feature)
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
                TerraformReportGenerator(logger, planData).generate(reportFile)

                var plannedProtectedResourceChanges = false
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