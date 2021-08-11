package jetbrains.buildServer.terraformSupportPlugin.beans

import jetbrains.buildServer.terraformSupportPlugin.TerraformFeatureConstants

class ProtectedResourcesBean {
    val key = TerraformFeatureConstants.FEATURE_SETTING_PROTECTED_RESOURCES
    val label = "Protected resource types [WIP]:"
    val description = "Create build problem if any resource type from the list (comma-separated) is planned for destroy"
}