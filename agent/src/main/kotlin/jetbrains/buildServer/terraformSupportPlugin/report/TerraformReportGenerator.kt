package jetbrains.buildServer.terraformSupportPlugin.report

import io.pebbletemplates.pebble.PebbleEngine
import io.pebbletemplates.pebble.loader.ClasspathLoader
import io.pebbletemplates.pebble.template.PebbleTemplate
import jetbrains.buildServer.agent.BuildProgressLogger
import jetbrains.buildServer.terraformSupportPlugin.TerraformFeatureConstants
import jetbrains.buildServer.terraformSupportPlugin.jsonOutput.model.PlanData
import java.io.File
import java.io.FileWriter

class TerraformReportGenerator(
    private val myLogger: BuildProgressLogger,
    private val myPlanData: PlanData
) {
    private fun getTemplate(): PebbleTemplate {
        val resourcesLoader = ClasspathLoader()
        resourcesLoader.prefix = TerraformFeatureConstants.REPORT_RESOURCE_FOLDER_PATH

        val engine = PebbleEngine.Builder()
            .extension(IndentationExtension())
            .loader(resourcesLoader)
            .build()

        try {
            return engine.getTemplate(TerraformFeatureConstants.REPORT_TEMPLATE_FILE)
        } catch (e: Exception) {
            myLogger.warning(e.stackTraceToString())
            throw(e)
        }
    }

    fun generate(reportFile: File): String {
        myLogger.message("Generating report for ${myPlanData.fileName}")

        val writer = FileWriter(reportFile)
        getTemplate().evaluate(writer, mapOf("planData" to myPlanData))

        return reportFile.absolutePath
    }

}