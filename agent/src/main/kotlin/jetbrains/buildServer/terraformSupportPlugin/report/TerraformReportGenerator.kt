package jetbrains.buildServer.terraformSupportPlugin.report

import com.google.gson.GsonBuilder
import com.samskivert.mustache.Mustache
import com.samskivert.mustache.Template
import jetbrains.buildServer.agent.AgentRunningBuild
import jetbrains.buildServer.agent.BuildProgressLogger
import jetbrains.buildServer.terraformSupportPlugin.TerraformFeatureConstants
import jetbrains.buildServer.terraformSupportPlugin.parsing.PlanData
import java.io.File

class TerraformReportGenerator(
    private val myBuild: AgentRunningBuild,
    private val myLogger: BuildProgressLogger,
    private val myPlanDataList: List<PlanData>
) {
    private fun getTemplate(): Template {
        val fullPath =
            "${TerraformFeatureConstants.REPORT_RESOURCE_FOLDER_PATH}${File.separator}${TerraformFeatureConstants.REPORT_TEMPLATE_FILE}"
        return when (val resource = TerraformReportGenerator::class.java.getResource(fullPath)) {
            null -> {
                throw IllegalArgumentException("File $fullPath was not found")
            }
            else -> {
                Mustache.compiler().compile(
                    resource.readText()
                )
            }
        }
    }

    private val myTemplate = getTemplate()

    private fun getReportPath(): String {
        return File(
            myBuild.agentTempDirectory,
            TerraformFeatureConstants.HIDDEN_ARTIFACT_REPORT_FILENAME
        ).absolutePath
    }

    fun generate(): String {
        myLogger.debug("Generating report: ${myPlanDataList.size} plans detected")

        GsonBuilder().setPrettyPrinting().create()
        val reportFile = File(getReportPath())

        for (planData in myPlanDataList) {
            reportFile.appendText(
                myTemplate.execute(planData)
            )
        }

        return reportFile.absolutePath
    }

}