package jetbrains.buildServer.terraformSupportPlugin.beans

import jetbrains.buildServer.terraformSupportPlugin.TerraformFeatureConstants

class UpdateBuildStatusBean {
    val key = TerraformFeatureConstants.FEATURE_SETTING_UPDATE_BUILD_STATUS
    val label = "Update build status [WIP]:"
    val description = "Update build status with plan results"
}