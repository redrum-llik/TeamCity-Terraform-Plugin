package jetbrains.buildServer.terraformSupportPlugin.beans

import jetbrains.buildServer.terraformSupportPlugin.TerraformFeatureConstants

class DoInitBean {
    val key = TerraformFeatureConstants.FEATURE_SETTING_DO_INIT_KEY
    val label = "Init:"
    val description = "Run \"terraform init\" command at the start of build"
}