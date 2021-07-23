package jetbrains.buildServer.terraformSupportPlugin.beans

import jetbrains.buildServer.terraformSupportPlugin.TerraformFeatureConstants

class CreateWorkspaceBean {
    val key = TerraformFeatureConstants.FEATURE_SETTING_CREATE_WORKSPACE_IF_NOT_FOUND_KEY
    val label = "Create if not found:"
    val description = "Create specified workspace if it was not found"
}