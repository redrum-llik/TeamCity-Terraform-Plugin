package jetbrains.buildServer.agent.terraformRunner

object TerraformCommandLineConstants {
    // terraform commands and parameters

    const val COMMAND_TERRAFORM = "terraform"
    const val COMMAND_TFENV = "tfenv"

    const val PARAM_VERSION = "-version"
    const val PARAM_CUSTOM_OUT = "-out"
    const val PARAM_CUSTOM_BACKUP_OUT = "-backup"
    const val PARAM_AUTO_APPROVE = "-auto-approve"
    const val PARAM_VAR_FILE = "-var-file"

    const val PARAM_COMMAND_INSTALL = "install"
    const val PARAM_COMMAND_USE = "use"

    const val PARAM_COMMAND_SELECT = "select"
    const val PARAM_COMMAND_NEW = "new"
}