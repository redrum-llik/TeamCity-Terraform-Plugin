package jetbrains.buildServer.runner.terraformRunner.beans

import jetbrains.buildServer.runner.terraform.TerraformRunnerConstants

class PassSystemParametersBean {
    val key = TerraformRunnerConstants.RUNNER_SETTING_PASS_SYSTEM_PARAMS
    val label = "Pass system parameters:"
    val description = "Save system parameters to file and pass them as -var-file"
}