package jetbrains.buildServer.terraformSupportPlugin

import jetbrains.buildServer.terraformSupportPlugin.TerraformFeatureConstants as FeatureConst

class TerraformFeatureConfiguration(private val properties: Map<String, String>) {
    fun useTfEnv(): Boolean {
        return properties[FeatureConst.FEATURE_SETTING_USE_TFENV].toBoolean()
    }

    fun getTfEnvPathParameter(): String? {
        return properties[FeatureConst.FEATURE_SETTING_TFENV_TOOL_VERSION]
    }

    fun getTerraformVersion(): String? {
        return properties[FeatureConst.FEATURE_SETTING_TERRAFORM_VERSION]
    }

    fun isReportEnabled(): Boolean {
        return !getPlanFile().isNullOrEmpty()
    }

    fun getPlanFile(): String? {
        return properties[FeatureConst.FEATURE_SETTING_PLAN_FILE]
    }

    fun isCustomWorkingDir(): Boolean {
        return !getTerraformWorkingDir().isNullOrEmpty()
    }

    fun getTerraformWorkingDir(): String? {
        return properties[FeatureConst.FEATURE_SETTING_TERRAFORM_WORKING_DIRECTORY]
    }

    fun hasProtectedResources(): Boolean {
        return getProtectedResources().isNotEmpty()
    }

    fun getProtectedResources(): List<String> {
        val rawString = properties[FeatureConst.FEATURE_SETTING_PROTECTED_RESOURCES]
        return when {
            !rawString.isNullOrEmpty() -> {
                rawString.split(",").toList()
            }
            else -> {
                listOf()
            }
        }
    }

    fun updateBuildStatus(): Boolean {
        return properties[FeatureConst.FEATURE_SETTING_UPDATE_BUILD_STATUS].toBoolean()
    }

    fun exportSystemProperties(): Boolean {
        return !systemPropertiesOutFile().isNullOrEmpty()
    }

    fun systemPropertiesOutFile(): String? {
        return properties[FeatureConst.FEATURE_SETTING_SYSTEM_PROPERTIES]
    }
}