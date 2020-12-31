package jetbrains.buildServer.runner.terraformRunner

import jetbrains.buildServer.runner.terraform.TerraformRunnerConstants as CommonConst

class TerraformBean {
    val commandKey: String = CommonConst.PARAM_COMMAND_KEY
    val commandInit: String = CommonConst.PARAM_COMMAND_INIT
    val commandPlan: String = CommonConst.PARAM_COMMAND_PLAN
    val commandApply: String = CommonConst.PARAM_COMMAND_APPLY
}