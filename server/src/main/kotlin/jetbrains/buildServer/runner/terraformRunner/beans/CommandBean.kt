package jetbrains.buildServer.runner.terraformRunner.beans

import jetbrains.buildServer.runner.terraform.TerraformCommandType
import jetbrains.buildServer.runner.terraform.TerraformRunnerConstants

class CommandBean {
    val key = TerraformRunnerConstants.RUNNER_SETTING_COMMAND_KEY
    val label = "Command:"

    val planKey = TerraformCommandType.PLAN.name
    val planValue = TerraformCommandType.PLAN.id
    val applyKey = TerraformCommandType.APPLY.name
    val applyValue =TerraformCommandType.APPLY.id
    val customKey = TerraformCommandType.CUSTOM.name
    val customValue = TerraformCommandType.CUSTOM.id

    val customCommandKey = TerraformRunnerConstants.RUNNER_SETTING_CUSTOM_COMMAND_KEY
    val customCommandLabel = "Custom command:"
    val customCommandDescription = "Custom Terraform command to invoke"
}