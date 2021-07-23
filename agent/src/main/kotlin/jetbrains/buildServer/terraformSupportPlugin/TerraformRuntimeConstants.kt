package jetbrains.buildServer.terraformSupportPlugin

object TerraformRuntimeConstants {
    // terraform commands and parameters

    const val COMMAND_TERRAFORM = "terraform"
    const val COMMAND_TFENV = "tfenv"

    // terraform parameters
    const val PARAM_COMMAND_INIT = "init"
    const val PARAM_COMMAND_SHOW = "show"
    const val PARAM_COMMAND_JSON = "-json"
    const val PARAM_COMMAND_VERSION = "version"

    // tfenv parameters
    const val PARAM_COMMAND_INSTALL = "install"
    const val PARAM_COMMAND_USE = "use"

    // terraform workspace parameters
    const val PARAM_COMMAND_WORKSPACE = "workspace"
    const val PARAM_COMMAND_SELECT = "select"
    const val PARAM_COMMAND_NEW = "new"

    const val ENV_TF_LOG = "TF_LOG_CORE"
    const val ENV_TF_LOG_INFO = "INFO"
    const val ENV_TF_LOG_PATH = "TF_LOG_PATH"
}