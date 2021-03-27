package jetbrains.buildServer.runner.terraform

class TerraformRunnerInstanceConfiguration(private val properties: Map<String, String>) {

    fun getVersionMode(): TerraformVersionMode {
        return TerraformVersionMode.valueOf(
                properties[TerraformRunnerConstants.RUNNER_SETTING_VERSION_KEY]
                        ?: error("Version mode is not set")
        )
    }

    fun getTFEnvVersion(): String? {
        if (getVersionMode() != TerraformVersionMode.TFENV) {
            throw IllegalAccessError("Attempted to access the version parameter while using different version mode")
        }
        return properties[TerraformRunnerConstants.RUNNER_SETTING_VERSION_TFENV_VERSION]
    }

    fun getCommand(): TerraformCommandType {
        return TerraformCommandType.valueOf(
                properties[TerraformRunnerConstants.RUNNER_SETTING_COMMAND_KEY]
                        ?: error("Command is not set")
        )
    }

    fun checkType(other: TerraformCommandType): Boolean {
        return getCommand() == other
    }

    private fun throwIfIncorrectType(other: TerraformCommandType) {
        if (!checkType(other)) {
            throw IllegalAccessError("This parameter is not available under the currently selected command (${getCommand()})")
        }
    }

    // plan-specific parameters

    fun getPlanDoDestroy(): Boolean {
        throwIfIncorrectType(TerraformCommandType.PLAN)
        return properties[TerraformRunnerConstants.RUNNER_SETTING_PLAN_DO_DESTROY_KEY].toBoolean()
    }

    fun getPlanDoInit(): Boolean {
        throwIfIncorrectType(TerraformCommandType.PLAN)
        return properties[TerraformRunnerConstants.RUNNER_SETTING_PLAN_DO_INIT_KEY].toBoolean()
    }

    fun getPlanCustomOut(): String? {
        throwIfIncorrectType(TerraformCommandType.PLAN)
        return properties[TerraformRunnerConstants.RUNNER_SETTING_PLAN_CUSTOM_OUT_KEY]
    }

    // apply-specific parameters

    fun getApplyCustomBackupOut(): String? {
        throwIfIncorrectType(TerraformCommandType.APPLY)
        return properties[TerraformRunnerConstants.RUNNER_SETTING_APPLY_CUSTOM_BACKUP_KEY]
    }

    fun getApplyDoAutoApprove(): Boolean {
        throwIfIncorrectType(TerraformCommandType.APPLY)
        return properties[TerraformRunnerConstants.RUNNER_SETTING_APPLY_DO_AUTO_APPROVE].toBoolean()
    }

    // common parameters

    fun getExtraArgs(): String? {
        return properties[TerraformRunnerConstants.RUNNER_SETTING_EXTRA_ARGS]
    }

    fun getDoColor(): Boolean {
        return properties[TerraformRunnerConstants.RUNNER_SETTING_DO_COLOR_KEY].toBoolean()
    }

    fun getDoPassConfigParams(): Boolean {
        return properties[TerraformRunnerConstants.RUNNER_SETTING_DO_PASS_CONFIG_PARAMS].toBoolean()
    }
}