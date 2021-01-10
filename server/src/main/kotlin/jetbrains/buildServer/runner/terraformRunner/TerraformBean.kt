package jetbrains.buildServer.runner.terraformRunner

import jetbrains.buildServer.runner.terraform.TerraformRunnerConstants as CommonConst
import jetbrains.buildServer.runner.terraform.TerraformCommandType as CommandType

class TerraformBean {
    val commandKey: String = CommonConst.RUNNER_SETTING_COMMAND_KEY
    val commandInit: String = CommandType.Init.id
    val commandPlan: String = CommandType.Plan.id
    val commandApply: String = CommandType.Apply.id

    val planDoDestroyKey: String = CommonConst.RUNNER_SETTING_PLAN_DO_DESTROY_KEY
    val planDoInitKey: String = CommonConst.RUNNER_SETTING_PLAN_DO_INIT_KEY
    val planCustomOutputPathKey: String = CommonConst.RUNNER_SETTING_PLAN_CUSTOM_OUT_KEY

    val applyCustomBackupPathKey: String = CommonConst.RUNNER_SETTING_APPLY_CUSTOM_BACKUP_KEY
    val applyDoAutoApproveKey: String = CommonConst.RUNNER_SETTING_APPLY_DO_AUTO_APPROVE

    val extraArgsKey: String = CommonConst.RUNNER_SETTING_EXTRA_ARGS
    val doColorKey: String = CommonConst.RUNNER_SETTING_DO_COLOR_KEY
}