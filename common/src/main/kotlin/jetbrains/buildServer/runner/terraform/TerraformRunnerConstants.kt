package jetbrains.buildServer.runner.terraform

object TerraformRunnerConstants {
    // plugin-level data
    const val RUNNER_DESCRIPTION = "Runner for Terraform CLI commands execution"
    const val RUNNER_DISPLAY_NAME = "Terraform"
    const val RUNNER_TYPE = "terraform-runner"

    // runner parameters bean data
    const val PARAM_COMMAND_KEY = "Command"
    const val PARAM_COMMAND_INIT = "init"
    const val PARAM_COMMAND_PLAN = "plan"
    const val PARAM_COMMAND_APPLY = "apply"


}