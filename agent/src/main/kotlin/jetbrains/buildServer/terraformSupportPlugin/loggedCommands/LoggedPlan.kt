package jetbrains.buildServer.terraformSupportPlugin.loggedCommands

import jetbrains.buildServer.terraformSupportPlugin.TerraformRuntimeConstants

class LoggedPlan(
    myExecutable: String,
    myCommand: String,
    myArguments: List<String>,
    private val myOutFile: String?
) : LoggedTerraformCommand(myExecutable, myCommand, myArguments) {
    override fun producedFile(): Boolean {
        return !myOutFile.isNullOrEmpty()
    }

    override fun getProducedFileName(): String {
        return myOutFile!!
    }
}