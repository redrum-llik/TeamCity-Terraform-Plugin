package jetbrains.buildServer.terraformSupportPlugin.beans

import jetbrains.buildServer.terraformSupportPlugin.TerraformFeatureConstants

class TargetTerraformVersionBean {
    val key = TerraformFeatureConstants.FEATURE_SETTING_TERRAFORM_VERSION
    val label = "Terraform version:"
    val description = "Try to install (or switch to) specified version; leave empty for auto-detection of target version"
}