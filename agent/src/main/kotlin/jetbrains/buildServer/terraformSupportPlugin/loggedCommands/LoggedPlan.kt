package jetbrains.buildServer.terraformSupportPlugin.loggedCommands

import jetbrains.buildServer.terraformSupportPlugin.TerraformRuntimeConstants

class LoggedPlan(
    myExecutable: String,
    myCommand: String,
    myArguments: List<String>
) : LoggedTerraformCommand(myExecutable, myCommand, myArguments) {
    override fun producedFile(): Boolean {
        return getArguments().any { it.contains(TerraformRuntimeConstants.PARAM_COMMAND_OUT) }
    }

    override fun getProducedFileName(): String {
        return getArguments()
            .first { it.contains(TerraformRuntimeConstants.PARAM_COMMAND_OUT) }
            .removePrefix(TerraformRuntimeConstants.PARAM_COMMAND_OUT)

    }
}