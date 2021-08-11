package jetbrains.buildServer.terraformSupportPlugin

object TerraformRuntimeConstants {
    // terraform commands and parameters

    const val COMMAND_TERRAFORM = "terraform"
    const val COMMAND_TFENV = "tfenv"

    // terraform parameters
    const val PARAM_COMMAND_SHOW = "show"
    const val PARAM_COMMAND_JSON = "-json"

    // tfenv parameters
    const val PARAM_COMMAND_INSTALL = "install"
    const val PARAM_COMMAND_USE = "use"
}