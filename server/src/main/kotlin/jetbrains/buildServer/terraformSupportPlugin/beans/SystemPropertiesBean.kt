package jetbrains.buildServer.terraformSupportPlugin.beans

import jetbrains.buildServer.terraformSupportPlugin.TerraformFeatureConstants

class SystemPropertiesBean {
    val key = TerraformFeatureConstants.FEATURE_SETTING_SYSTEM_PROPERTIES
    val label = "Pass system properties:"
    val description = "Save system properties to specified path which may be used with -var-file"
}