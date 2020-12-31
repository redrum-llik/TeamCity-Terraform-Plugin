package jetbrains.buildServer.runner.terraformRunner

import jetbrains.buildServer.runner.terraform.TerraformRunnerConstants as CommonConst

class TerraformBean {
    val commandKey: String = CommonConst.RUNNER_SETTING_COMMAND_KEY
    val commandInit: String = CommonConst.RUNNER_SETTING_COMMAND_INIT
    val commandPlan: String = CommonConst.RUNNER_SETTING_COMMAND_PLAN
    val commandApply: String = CommonConst.RUNNER_SETTING_COMMAND_APPLY
}