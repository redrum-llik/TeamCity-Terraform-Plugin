package jetbrains.buildServer.terraformSupportPlugin.loggedCommands

abstract class LoggedTerraformCommand(val arguments: List<String>) {
    abstract fun producedFile(): Boolean

    abstract fun getProducedFile(): Boolean
}