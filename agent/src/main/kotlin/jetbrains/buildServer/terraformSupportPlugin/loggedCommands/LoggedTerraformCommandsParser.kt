package jetbrains.buildServer.terraformSupportPlugin.loggedCommands

class LoggedTerraformCommandsParser(val logFilePath: String) {
    private val myParsedCommands = parseLoggedCommands()

    private fun parseLoggedCommands(): List<LoggedTerraformCommand> {
        TODO("Not yet implemented")
    }

    fun getPlanCommands(): List<LoggedPlan> {
        TODO("Not yet implemented")
    }

    fun getShowCommands(): List<LoggedShow> {
        TODO("Not yet implemented")
    }
}