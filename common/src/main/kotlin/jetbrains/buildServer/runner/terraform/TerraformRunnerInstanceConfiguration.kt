package jetbrains.buildServer.runner.terraform

class TerraformRunnerInstanceConfiguration(private val properties: Map<String, String>) {
    fun getCommand(): TerraformCommandType {
        return TerraformCommandType.valueOf(
                (properties[TerraformRunnerConstants.RUNNER_SETTING_COMMAND_KEY]
                        ?: error("Specified value is not supported for this property")
                        )
                        .capitalize()
        )
    }

    fun checkType(other: TerraformCommandType): Boolean {
        return getCommand() == other
    }

    private fun throwIfIncorrectType(other: TerraformCommandType) {
        if (!checkType(other)) {
            throw IllegalAccessError("This parameter is not available under the currently selected command (${getCommand().id})")
        }
    }

    // plan-specific parameters

    fun getPlanDoDestroy(): Boolean {
        throwIfIncorrectType(TerraformCommandType.Plan)
        return properties[TerraformRunnerConstants.RUNNER_SETTING_PLAN_DO_DESTROY_KEY].toBoolean()
    }

    fun getPlanDoInit(): Boolean {
        throwIfIncorrectType(TerraformCommandType.Plan)
        return properties[TerraformRunnerConstants.RUNNER_SETTING_PLAN_DO_INIT_KEY].toBoolean()
    }

    fun getPlanCustomOut(): String? {
        throwIfIncorrectType(TerraformCommandType.Plan)
        return properties[TerraformRunnerConstants.RUNNER_SETTING_PLAN_CUSTOM_OUT_KEY]
    }

    // apply-specific parameters

    fun getApplyCustomBackupOut(): String? {
        throwIfIncorrectType(TerraformCommandType.Apply)
        return properties[TerraformRunnerConstants.RUNNER_SETTING_APPLY_CUSTOM_BACKUP_KEY]
    }

    fun getApplyDoAutoApprove(): Boolean {
        throwIfIncorrectType(TerraformCommandType.Apply)
        return properties[TerraformRunnerConstants.RUNNER_SETTING_APPLY_DO_AUTO_APPROVE].toBoolean()
    }

    // common parameters

    fun getDoColor(): Boolean {
        return properties[TerraformRunnerConstants.RUNNER_SETTING_DO_COLOR_KEY].toBoolean()
    }
}