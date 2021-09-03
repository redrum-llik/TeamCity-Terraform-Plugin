package jetbrains.buildServer.terraformSupportPlugin

import jetbrains.buildServer.log.Loggers
import jetbrains.buildServer.serverSide.BuildsManager
import jetbrains.buildServer.serverSide.SBuild
import jetbrains.buildServer.serverSide.artifacts.BuildArtifact
import jetbrains.buildServer.serverSide.artifacts.BuildArtifactsViewMode
import jetbrains.buildServer.web.openapi.BuildTab
import jetbrains.buildServer.web.openapi.PluginDescriptor
import jetbrains.buildServer.web.openapi.WebControllerManager
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.stream.Collectors


class TerraformReportTab(
    manager: WebControllerManager,
    buildManager: BuildsManager,
    private val descriptor: PluginDescriptor
) : BuildTab(
    "terraformChangesReport",
    "Terraform Plan",
    manager,
    buildManager,
    descriptor.getPluginResourcesPath("terraformChangesReport.jsp")
) {
    init {
        addCssFile(descriptor.getPluginResourcesPath("terraformChangesReport.css"))
    }

    override fun isAvailableFor(build: SBuild): Boolean {
        return getTerraformReportFromBuildArtifacts(build) != null
    }

    private fun getTerraformReportFromBuildArtifacts(build: SBuild): BuildArtifact? {
        return try {
            val hiddenArtifacts = build.getArtifacts(
                BuildArtifactsViewMode.VIEW_HIDDEN_ONLY
            )
            hiddenArtifacts.getArtifact(
                "${TerraformFeatureConstants.HIDDEN_ARTIFACT_REPORT_FOLDER}${File.separator}${TerraformFeatureConstants.HIDDEN_ARTIFACT_REPORT_FILENAME}"
            )
        } catch (e: AccessDeniedException) {
            null
        }
    }

    override fun fillModel(model: MutableMap<String, Any>, build: SBuild) {
        val reportArtifact = getTerraformReportFromBuildArtifacts(build)
        try {
            val inputStream = reportArtifact!!.inputStream // isAvailableFor is evaluated before filling the model
            val reportString = BufferedReader(
                InputStreamReader(inputStream)
            ).lines().collect(
                Collectors.joining("\n")
            )
            model["reportString"] = reportString
        } catch (e: Exception) {
            Loggers.SERVER.warnAndDebugDetails(
                "Cannot process Terraform report artifact to show it in the build tab",
                e
            )
        }
    }
}