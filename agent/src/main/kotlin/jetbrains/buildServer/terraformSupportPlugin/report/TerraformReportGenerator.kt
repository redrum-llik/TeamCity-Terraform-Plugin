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
    private val myPlanData: PlanData
) {
    private fun getTemplate(): Template {
        val fullPath =
            "${TerraformFeatureConstants.REPORT_RESOURCE_FOLDER_PATH}${File.separator}${TerraformFeatureConstants.REPORT_TEMPLATE_FILE}"
        return when (val resource = TerraformReportGenerator::class.java.getResource(fullPath)) {
            null -> {
                throw IllegalArgumentException("File $fullPath was not found in plugin resources")
            }
            else -> {
                Mustache.compiler().compile(
                    resource.readText()
                )
            }
        }
    }

    private val myTemplate = getTemplate()

    fun generate(reportFile: File): String {
        myLogger.debug("Generating report for ${myPlanData.fileName}")

        GsonBuilder().setPrettyPrinting().create()
        reportFile.writeText(
            myTemplate.execute(myPlanData)
        )

        return reportFile.absolutePath
    }

}