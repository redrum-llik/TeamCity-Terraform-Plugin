package jetbrains.buildServer.terraformSupportPlugin.beans

import jetbrains.buildServer.terraformSupportPlugin.TerraformFeatureConstants

class CustomWorkingDirBean {
    val key = TerraformFeatureConstants.FEATURE_SETTING_CUSTOM_WORKING_DIR_KEY
    val label = "Custom working dir:"
    val description = "Terraform data path (relative to the checkout directory)"
}