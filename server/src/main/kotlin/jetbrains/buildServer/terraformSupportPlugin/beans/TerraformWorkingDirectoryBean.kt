package jetbrains.buildServer.terraformSupportPlugin.beans

import jetbrains.buildServer.terraformSupportPlugin.TerraformFeatureConstants

class TerraformWorkingDirectoryBean {
    val key = TerraformFeatureConstants.FEATURE_SETTING_TERRAFORM_WORKING_DIRECTORY
    val label = "Terraform configuration path"
    val description = "Optional, set if Terraform configuration directory differs from the checkout directory"
}