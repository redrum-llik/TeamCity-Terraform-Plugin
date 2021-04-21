package jetbrains.buildServer.runner.terraformRunner.beans

import jetbrains.buildServer.runner.terraform.TerraformRunnerConstants

class PassSystemPropertiesBean {
    val key = TerraformRunnerConstants.RUNNER_SETTING_PASS_SYSTEM_PARAMS
    val label = "Pass system properties:"
    val description = "Save system properties to file and pass them as -var-file"
}