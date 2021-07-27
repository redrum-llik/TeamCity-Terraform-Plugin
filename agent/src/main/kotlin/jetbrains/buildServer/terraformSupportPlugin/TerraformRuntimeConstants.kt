package jetbrains.buildServer.terraformSupportPlugin

object TerraformRuntimeConstants {
    // terraform commands and parameters

    const val COMMAND_TERRAFORM = "terraform"
    const val COMMAND_TFENV = "tfenv"

    // terraform parameters
    const val PARAM_COMMAND_PLAN = "plan"
    const val PARAM_COMMAND_SHOW = "show"
    const val PARAM_COMMAND_VERSION = "version"
    const val PARAM_COMMAND_JSON = "-json"
    const val PARAM_COMMAND_OUT = "-out"

    val TERRAFORM_LOG_CLI_ARGS_REGEX = ".+CLI args: \\[]string\\{(.*)}".toRegex()

    // tfenv parameters
    const val PARAM_COMMAND_INSTALL = "install"
    const val PARAM_COMMAND_USE = "use"

    const val ENV_TF_LOG = "TF_LOG_CORE"
    const val ENV_TF_LOG_LEVEL = "INFO"
    const val ENV_TF_LOG_PATH = "TF_LOG_PATH"
}