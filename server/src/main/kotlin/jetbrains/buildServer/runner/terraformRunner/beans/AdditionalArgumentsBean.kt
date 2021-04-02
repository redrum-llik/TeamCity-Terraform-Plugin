package jetbrains.buildServer.runner.terraformRunner.beans

import jetbrains.buildServer.runner.terraform.TerraformRunnerConstants

class AdditionalArgumentsBean {
    val key = TerraformRunnerConstants.RUNNER_SETTING_ADDITIONAL_ARGS
    val label = "Additional arguments:"
    val description = "Additional arguments to be passed to the command"
}