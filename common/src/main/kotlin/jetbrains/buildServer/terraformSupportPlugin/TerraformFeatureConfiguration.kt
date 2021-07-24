package jetbrains.buildServer.terraformSupportPlugin

class TerraformFeatureConfiguration(private val properties: Map<String, String>) {
    fun useTfEnv(): Boolean {
        return properties[TerraformFeatureConstants.FEATURE_SETTING_USE_TFENV].toBoolean()
    }

    fun getTfEnvPathParameter(): String? {
        return properties[TerraformFeatureConstants.FEATURE_SETTING_TFENV_TOOL_VERSION]
    }

    fun getTerraformVersion(): String? {
        return properties[TerraformFeatureConstants.FEATURE_SETTING_TERRAFORM_VERSION]
    }

    fun exportSystemProperties(): Boolean {
        return !systemPropertiesOutFile().isNullOrEmpty()
    }

    fun systemPropertiesOutFile(): String? {
        return properties[TerraformFeatureConstants.FEATURE_SETTING_SYSTEM_PROPERTIES]
    }
}