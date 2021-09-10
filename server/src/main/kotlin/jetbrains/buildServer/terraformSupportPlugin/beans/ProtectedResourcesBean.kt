package jetbrains.buildServer.terraformSupportPlugin.beans

import jetbrains.buildServer.terraformSupportPlugin.TerraformFeatureConstants

class ProtectedResourcesBean {
    val key = TerraformFeatureConstants.FEATURE_SETTING_PROTECTED_RESOURCES
    val label = "Protected resource types:"
    val description = "Create build problem if any resource type matching Java regex pattern is planned for destroy"
}