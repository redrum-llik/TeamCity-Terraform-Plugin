package jetbrains.buildServer.agent.terraformRunner.cmd

import com.intellij.openapi.diagnostic.Logger
import jetbrains.buildServer.agent.runner.ProgramCommandLine
import jetbrains.buildServer.agent.runner.SimpleProgramCommandLine

class CommandLineBuilder {
    private val LOG = Logger.getInstance(this.javaClass.name)
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
        when {
            argName.isNullOrEmpty() && value.isNullOrEmpty() -> {
                return
            }
            argName.isNullOrEmpty() -> {
                arguments.add(value as String)
            }
            value.isNullOrEmpty() -> {
                arguments.add(argName)
            }
            else -> {
                arguments.add("$argName=$value")
            }
        }
    }

    fun setEnvironment(newEnvironment: Map<String, String>) {
        environment.putAll(newEnvironment)
    }

    fun addEnvironmentVariable(varName: String, value: String = String()) {
        if (environment.containsKey(varName)) {
            LOG.warn("Overriding environment variable $varName with value $value")
        } else {
            LOG.debug("Adding environment variable $varName: $value")
        }
        environment[varName] = value
    }
}