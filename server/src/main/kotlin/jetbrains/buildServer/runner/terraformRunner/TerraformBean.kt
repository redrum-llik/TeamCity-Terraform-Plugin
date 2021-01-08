package jetbrains.buildServer.runner.terraformRunner

import jetbrains.buildServer.runner.terraform.TerraformRunnerConstants as CommonConst
import jetbrains.buildServer.runner.terraform.TerraformCommandType as CommandType

class TerraformBean {
    val commandKey: String = CommonConst.RUNNER_SETTING_COMMAND_KEY
    val commandInit: String = CommandType.Init.id
    val commandPlan: String = CommandType.Plan.id
    val commandApply: String = CommandType.Apply.id
}