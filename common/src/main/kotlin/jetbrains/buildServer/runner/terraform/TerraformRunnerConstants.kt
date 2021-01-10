package jetbrains.buildServer.runner.terraform

object TerraformRunnerConstants {
        // plugin-level data
    const val RUNNER_DESCRIPTION = "Runner for Terraform CLI commands execution"
    const val RUNNER_DISPLAY_NAME = "Terraform"
    const val RUNNER_TYPE = "terraform-runner"

    // runner parameters bean data
    const val RUNNER_SETTING_COMMAND_KEY = "Command"
    const val RUNNER_SETTING_PLAN_CUSTOM_OUT_KEY = "Custom output"
    const val RUNNER_SETTING_PLAN_DO_INIT_KEY = "Init"
    const val RUNNER_SETTING_PLAN_DO_DESTROY_KEY = "Destroy"
    const val RUNNER_SETTING_APPLY_DO_AUTO_APPROVE = "Auto-approve"
    const val RUNNER_SETTING_APPLY_CUSTOM_BACKUP_KEY = "Custom backup"
    const val RUNNER_SETTING_EXTRA_ARGS = "Additional arguments"
    const val RUNNER_SETTING_DO_COLOR_KEY = "Enable color"

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