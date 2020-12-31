package jetbrains.buildServer.runner.terraform

object TerraformRunnerConstants {
    // plugin-level data
    const val RUNNER_DESCRIPTION = "Runner for Terraform CLI commands execution"
    const val RUNNER_DISPLAY_NAME = "Terraform"
    const val RUNNER_TYPE = "terraform-runner"

    // runner parameters bean data
    const val RUNNER_SETTING_COMMAND_KEY = "Command"
    const val RUNNER_SETTING_COMMAND_INIT = "init"
    const val RUNNER_SETTING_COMMAND_PLAN = "plan"
    const val RUNNER_SETTING_COMMAND_APPLY = "apply"

    // detection variables
    const val BUILD_PARAM_SEARCH_PATH = "teamcity.terraform.detector.search.paths"

    const val AGENT_PARAM_TERRAFORM_PREFIX = "Terraform"
    const val AGENT_PARAM_PATH_POSTFIX = "Path"
    const val AGENT_PARAM_PATH_VERSION = "Version"

    const val AGENT_PARAM_TERRAFORM_PATH = AGENT_PARAM_TERRAFORM_PREFIX +
            "_${AGENT_PARAM_PATH_POSTFIX}"
    const val AGENT_PARAM_TERRAFORM_VERSION = AGENT_PARAM_TERRAFORM_PREFIX +
            "_${AGENT_PARAM_PATH_VERSION}"

}

fun getVersionedPathVarName(version: String) : String {
    return "${TerraformRunnerConstants.AGENT_PARAM_TERRAFORM_PREFIX}" +
            "_${version}" +
            "_${TerraformRunnerConstants.AGENT_PARAM_PATH_POSTFIX}"
}