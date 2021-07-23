package jetbrains.buildServer.terraformSupportPlugin.cmd

import com.intellij.execution.configurations.GeneralCommandLine

class CommandLineBuilder {
    private val arguments: ArrayList<String> = ArrayList()
    private val environment: MutableMap<String, String> = HashMap()
    var workingDir: String = String()
    var executablePath: String = String()

    fun build(): GeneralCommandLine {
        if (executablePath.isEmpty()) {
            throw Exception("Executable path should be specified")
        }
        else if (workingDir.isEmpty()) {
            throw Exception("Working directory path should be specified")
        }

        val commandLine = GeneralCommandLine()
        commandLine.exePath = executablePath
        commandLine.setWorkDirectory(workingDir)
        commandLine.envParams = environment
        for (argument in arguments) {
            commandLine.addParameter(argument)
        }

        return commandLine
    }

    fun addArgument(argName: String? = null, value: String? = null) {
        if (!argName.isNullOrEmpty()) {
            arguments.add(argName)
        }

        if (!value.isNullOrEmpty()) {
            arguments.add(value)
        }
    }
}