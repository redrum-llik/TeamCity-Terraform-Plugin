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

    fun doInit(): Boolean {
        return properties[TerraformFeatureConstants.FEATURE_SETTING_DO_INIT_KEY].toBoolean()
    }

    fun useWorkspace(): Boolean {
        return !getWorkspaceName().isNullOrEmpty()
    }

    fun getWorkspaceName(): String? {
        return properties[TerraformFeatureConstants.FEATURE_SETTING_USE_WORKSPACE_KEY]
    }

    fun createWorkspaceIfNotFound(): Boolean {
        return properties[TerraformFeatureConstants.FEATURE_SETTING_CREATE_WORKSPACE_IF_NOT_FOUND_KEY].toBoolean()
    }

    fun runInitializationStage(): Boolean {
        return useWorkspace() || doInit() || useTfEnv()
    }

    fun useCustomWorkingDir(): Boolean {
        return customWorkingDirPath() != null
    }

    fun customWorkingDirPath(): String? {
        return properties[TerraformFeatureConstants.FEATURE_SETTING_CUSTOM_WORKING_DIR_KEY]
    }

    fun exportSystemProperties(): Boolean {
        return !systemPropertiesOutFile().isNullOrEmpty()
    }

    fun systemPropertiesOutFile(): String? {
        return properties[TerraformFeatureConstants.FEATURE_SETTING_SYSTEM_PROPERTIES]
    }
}