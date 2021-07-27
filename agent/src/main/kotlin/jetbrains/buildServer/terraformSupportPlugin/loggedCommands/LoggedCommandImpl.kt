package jetbrains.buildServer.terraformSupportPlugin.loggedCommands

class LoggedCommandImpl(
    myExecutable: String,
    myCommand: String,
    myArguments: List<String>
) : LoggedTerraformCommand(myExecutable, myCommand, myArguments) {
    override fun producedFile(): Boolean {
        return false
    }

    override fun getProducedFileName(): String {
        throw NotImplementedError("Never meant to be called; those commands do not produce any important files")
    }
}