package jetbrains.buildServer.terraformSupportPlugin.tfEnvTool

import jetbrains.buildServer.terraformSupportPlugin.TerraformFeatureConstants
import jetbrains.buildServer.tools.SimpleToolVersion
import jetbrains.buildServer.tools.ToolVersionIdHelper
import jetbrains.buildServer.tools.available.DownloadableToolVersion

class TfEnvToolVersion(val versionNumber: String): DownloadableToolVersion,
    SimpleToolVersion(TfEnvToolType.INSTANCE, versionNumber,
        ToolVersionIdHelper.getToolId(TfEnvToolType.INSTANCE, versionNumber),
        "${TerraformFeatureConstants.TFENV_TOOL_DISPLAY_NAME} $versionNumber"
    ) {

    override fun getDownloadUrl() = "https://github.com/tfutils/tfenv/archive/refs/tags/v${version}.zip"

    override fun getDestinationFileName() = "tfenv-${version}.zip"
}