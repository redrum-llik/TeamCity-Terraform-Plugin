package jetbrains.buildServer.terraformSupportPlugin.cmd

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.CapturingProcessHandler
import com.intellij.execution.process.ProcessNotCreatedException
import com.intellij.execution.process.ProcessOutput
import jetbrains.buildServer.agent.AgentRunningBuild
import jetbrains.buildServer.agent.BuildProgressLogger
import jetbrains.buildServer.agent.FlowLogger
import jetbrains.buildServer.terraformSupportPlugin.TerraformFeatureConfiguration
import jetbrains.buildServer.terraformSupportPlugin.TerraformRuntimeConstants
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Paths

abstract class BaseCommand(
    protected val myBuild: AgentRunningBuild,
    protected val myLogger: BuildProgressLogger,
    val myConfiguration: TerraformFeatureConfiguration
) {

    init {
        myLogger.debug("Initialized ${this.javaClass.simpleName} instance")
    }

    protected open fun getExecutablePath(): String {
        return TerraformRuntimeConstants.COMMAND_TERRAFORM
    }

    open fun getWorkingDir(): String {
        return when {
            myConfiguration.isCustomWorkingDir() -> {
                File(
                    myBuild.checkoutDirectory,
                    myConfiguration.getTerraformWorkingDir()!!
                ).absolutePath
            }
            else -> myBuild.checkoutDirectory.absolutePath
        }
    }

    abstract fun prepareArguments(builder: CommandLineBuilder): CommandLineBuilder

    abstract fun describe(): String

    private fun makeProgramCommandLine(): GeneralCommandLine {
        val builder = CommandLineBuilder()

        builder.executablePath = getExecutablePath()
        builder.workingDir = getWorkingDir()
        prepareArguments(builder)

        return builder.build()
    }

    private fun logProcessExitCode(exitCode: Int) {
        val message = "Command finished with code $exitCode"
        when (exitCode) {
            0 -> myLogger.message(message)
            else -> myLogger.error(message)
        }
    }

    protected open fun logProcessOutput(output: ProcessOutput) {
        //val stdOut = output.stdoutLines
        //val stdErr = output.stderrLines

        val stdOut = output.stdout
        val stdErr = output.stderr

        if (stdOut.isNotEmpty()) {
            myLogger.message(stdOut)
        }
        if (stdErr.isNotEmpty()) {
            myLogger.error(stdErr)
        }
    }

    open fun execute(): ProcessOutput {
        val commandLine = makeProgramCommandLine()
        myLogger.message("Starting ${commandLine.commandLineString} in ${commandLine.workDirectory}")

        val handler = CapturingProcessHandler(commandLine.createProcess(), StandardCharsets.UTF_8)
        val output = handler.runProcess()

        logProcessOutput(output)
        logProcessExitCode(output.exitCode)
        return output
    }
}