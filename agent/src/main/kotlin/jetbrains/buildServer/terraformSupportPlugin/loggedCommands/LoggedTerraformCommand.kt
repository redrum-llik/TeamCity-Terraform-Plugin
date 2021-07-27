package jetbrains.buildServer.terraformSupportPlugin.loggedCommands

abstract class LoggedTerraformCommand(
    private val myExecutable: String,
    private val myCommand: String,
    private val myArguments: List<String>
) {
    fun getExecutablePath(): String {
        return myExecutable
    }

    fun getCommand(): String {
        return myCommand
    }

    fun getArguments(): List<String> {
        return myArguments
    }

    abstract fun producedFile(): Boolean

    abstract fun getProducedFileName(): String
}