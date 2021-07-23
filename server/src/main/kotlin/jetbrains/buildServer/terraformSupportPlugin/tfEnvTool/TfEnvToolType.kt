package jetbrains.buildServer.terraformSupportPlugin.tfEnvTool

import jetbrains.buildServer.terraformSupportPlugin.TerraformFeatureConstants
import jetbrains.buildServer.tools.ToolTypeAdapter

class TfEnvToolType : ToolTypeAdapter() {

    override fun getType() = TerraformFeatureConstants.TFENV_TOOL_TYPE
    override fun getDisplayName() = TerraformFeatureConstants.TFENV_TOOL_DISPLAY_NAME
    override fun getDescription() = TerraformFeatureConstants.TFENV_TOOL_DESCRIPTION

    override fun isSupportDownload() = true

    override fun getValidPackageDescription() =
        """
            <p>
            The latest release version of tfenv can be 
            <a href="https://github.com/tfutils/tfenv/releases/latest" target="_blank" rel="noreferrer">downloaded from GitHub</a>.
            </p>
        """.trimIndent()


    companion object {
        val INSTANCE = TfEnvToolType()
    }
}