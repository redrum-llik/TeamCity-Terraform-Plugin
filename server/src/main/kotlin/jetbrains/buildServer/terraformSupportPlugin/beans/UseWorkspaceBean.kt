package jetbrains.buildServer.terraformSupportPlugin.beans

import jetbrains.buildServer.terraformSupportPlugin.TerraformFeatureConstants

class UseWorkspaceBean {
    val key = TerraformFeatureConstants.FEATURE_SETTING_USE_WORKSPACE_KEY
    val label = "Use workspace:"
    val description = "Switch to specified workspace at the start of the build"
}