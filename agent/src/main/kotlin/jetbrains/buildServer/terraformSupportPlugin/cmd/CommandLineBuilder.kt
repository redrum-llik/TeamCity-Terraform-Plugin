package jetbrains.buildServer.terraformSupportPlugin.cmd

import com.intellij.openapi.diagnostic.Logger
import jetbrains.buildServer.agent.runner.ProgramCommandLine
import jetbrains.buildServer.agent.runner.SimpleProgramCommandLine

class CommandLineBuilder {
    private val arguments: ArrayList<String> = ArrayList()
    private val environment: MutableMap<String, String> = HashMap()
    var workingDir: String = String()
    var executablePath: String = String()

    fun build(): ProgramCommandLine {
        when {
            executablePath.isEmpty() -> {
                throw Exception("Executable path should be specified")
            }
            workingDir.isEmpty() -> {
                throw Exception("Working directory path should be specified")
            }
            else -> return SimpleProgramCommandLine(
                    environment,
                    workingDir,
                    executablePath,
                    arguments
            )
        }
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