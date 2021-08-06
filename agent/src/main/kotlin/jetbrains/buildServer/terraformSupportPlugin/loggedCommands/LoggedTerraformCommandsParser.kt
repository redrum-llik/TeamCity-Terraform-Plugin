package jetbrains.buildServer.terraformSupportPlugin.loggedCommands

import jetbrains.buildServer.agent.BuildProgressLogger
import jetbrains.buildServer.terraformSupportPlugin.TerraformRuntimeConstants
import java.io.File
import java.util.stream.Collectors

class LoggedTerraformCommandsParser(private val logFilePath: String, private val myLogger: BuildProgressLogger) {
    private val myParsedCommands = parseLoggedCommands()

    private fun parseLoggedCommands(): List<LoggedTerraformCommand> {
        val logLines = File(logFilePath).useLines { it.toList() }
        val commands = mutableListOf<LoggedTerraformCommand>()

        for (line in logLines) {
            if (line.matches(TerraformRuntimeConstants.TERRAFORM_LOG_CLI_ARGS_REGEX)) {
                val arguments = TerraformRuntimeConstants.TERRAFORM_LOG_CLI_ARGS_REGEX.find(line)
                    ?.groupValues
                    ?.get(1)
                    ?.replace("\"", "") // arguments string has every argument encased in quotes
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
        val executable = arguments[0].trim()
        val command = arguments[1].trim()
        val commandArguments = arguments
            .subList(2, arguments.size)
            .stream()
            .map(String::trim)
            .collect(Collectors.toList())

        return if (command == TerraformRuntimeConstants.PARAM_COMMAND_PLAN) {
            val outArgumentIndex = commandArguments
                .indexOf(TerraformRuntimeConstants.PARAM_COMMAND_OUT)

            val outFile = try {
                commandArguments[outArgumentIndex + 1]
            } catch (e: IndexOutOfBoundsException) {
                myLogger.debug(
                    "Plan output file is missing from the registered command even though '"
                        + TerraformRuntimeConstants.PARAM_COMMAND_OUT + "' argument was supplied"
                )
                null
            }

            LoggedPlan(executable, command, commandArguments, outFile)
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