package jetbrains.buildServer.terraformSupportPlugin.beans

import jetbrains.buildServer.terraformSupportPlugin.TerraformRunnerConstants

class AdditionalArgumentsBean {
    val key = TerraformRunnerConstants.RUNNER_SETTING_ADDITIONAL_ARGS
    val label = "Additional arguments:"
    val description = "Additional arguments to be passed to the command"
}