package jetbrains.buildServer.runner.terraformRunner.beans

import jetbrains.buildServer.runner.terraform.TerraformRunnerConstants

class OutputPathBean {
    val key = TerraformRunnerConstants.RUNNER_SETTING_PLAN_CUSTOM_OUT_KEY
    val label = "Custom output path:"
    val description = "Custom path to the generated plan execution file, absolute or relative to the working directory"
}