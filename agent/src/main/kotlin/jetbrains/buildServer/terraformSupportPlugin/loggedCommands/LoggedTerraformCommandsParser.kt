package jetbrains.buildServer.terraformSupportPlugin.loggedCommands

import jetbrains.buildServer.agent.FlowLogger
import jetbrains.buildServer.terraformSupportPlugin.TerraformRuntimeConstants
import java.io.File

class LoggedTerraformCommandsParser(private val logFilePath: String, private val myLogger: FlowLogger) {
    private val myParsedCommands = parseLoggedCommands()

    private fun parseLoggedCommands(): List<LoggedTerraformCommand> {
        val logLines = File(logFilePath).useLines { it.toList() }
        val commands = mutableListOf<LoggedTerraformCommand>()

        for (line in logLines) {
            if (line.matches(TerraformRuntimeConstants.TERRAFORM_LOG_CLI_ARGS_REGEX)) {
                myLogger.debug("Detected command in the following log line: \n'${line}'")
                val arguments = TerraformRuntimeConstants.TERRAFORM_LOG_CLI_ARGS_REGEX.find(line)
                    ?.groupValues
                    ?.get(1)
                    ?.split(',')

                if (arguments != null) {
                    if (arguments.isNotEmpty()) {
                        commands.add(
                            parseCommand(arguments)
                        )
                    }
                }
            }
        }

        return commands
    }

    private fun parseCommand(arguments: List<String>): LoggedTerraformCommand {
        val executable = arguments[0]
        val command = arguments[1]
        val commandArguments = arguments.subList(2, arguments.size)

        return if (command == TerraformRuntimeConstants.PARAM_COMMAND_PLAN) {
            LoggedPlan(executable, command, commandArguments)
        } else {
            LoggedCommandImpl(executable, command, commandArguments)
        }
    }

    fun getCommands(): List<LoggedTerraformCommand> {
        return myParsedCommands
    }

    fun getPlanCommands(): List<LoggedPlan> {
        return myParsedCommands.filterIsInstance<LoggedPlan>()
    }
}