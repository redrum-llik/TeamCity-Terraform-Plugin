package jetbrains.buildServer.terraformSupportPlugin.beans

import jetbrains.buildServer.terraformSupportPlugin.TerraformFeatureConstants

class TfEnvVersionBean {
    val key = TerraformFeatureConstants.FEATURE_SETTING_TFENV_TOOL_VERSION
    val label = "tfEnv version:"

    val toolKey = TerraformFeatureConstants.TFENV_TOOL_TYPE
}