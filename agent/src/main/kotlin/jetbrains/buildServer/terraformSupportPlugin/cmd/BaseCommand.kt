package jetbrains.buildServer.terraformSupportPlugin.cmd

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.CapturingProcessHandler
import com.intellij.execution.process.ProcessOutput
import jetbrains.buildServer.agent.AgentRunningBuild
import jetbrains.buildServer.agent.FlowLogger
import jetbrains.buildServer.terraformSupportPlugin.TerraformFeatureConfiguration
import jetbrains.buildServer.terraformSupportPlugin.TerraformRuntimeConstants
import java.nio.charset.StandardCharsets
import java.nio.file.Paths

abstract class BaseCommand(
    protected val myBuild: AgentRunningBuild,
    protected val myLogger: FlowLogger,
    val myConfiguration: TerraformFeatureConfiguration
) {
    protected open fun getExecutablePath(): String {
        return TerraformRuntimeConstants.COMMAND_TERRAFORM
    }

    abstract fun prepareArguments(builder: CommandLineBuilder): CommandLineBuilder

    abstract fun describe() : String

    private fun makeProgramCommandLine(): GeneralCommandLine {
        val builder = CommandLineBuilder()

        builder.executablePath = getExecutablePath()

        val checkoutDirPath = myBuild.checkoutDirectory.absolutePath
        if (myConfiguration.useCustomWorkingDir()) {
            val workingDirPath = Paths.get(checkoutDirPath, myConfiguration.customWorkingDirPath()!!).normalize()
            builder.workingDir = workingDirPath.toAbsolutePath().toString()
        } else {
            builder.workingDir = checkoutDirPath
        }
        prepareArguments(builder)

        return builder.build()
    }

    fun logProcessExitCode(exitCode: Int) {
        val message = "Command finished with code $exitCode"
        when (exitCode) {
            0 -> myLogger.message(message)
            else -> myLogger.error(message)
        }
    }

    fun logProcessOutput(output: ProcessOutput) {
        val stdOut = output.stdoutLines
        val stdErr = output.stderrLines

        if (stdOut.isNotEmpty()) {
            val b = StringBuilder()
            for (line in stdOut) {
                b.appendLine(line)
            }
            myLogger.message(b.toString())
        }
        if (stdErr.isNotEmpty()) {
            val b = StringBuilder()
            for (line in stdErr) {
                b.appendLine(line)
            }
            myLogger.warning(b.toString())
        }
    }

    open fun execute(): ProcessOutput {
        val commandLine = makeProgramCommandLine()
        val handler = CapturingProcessHandler(commandLine.createProcess(), StandardCharsets.UTF_8)
        myLogger.message("Starting ${commandLine.commandLineString} in ${commandLine.workDirectory}")
        val output =  handler.runProcess()

        logProcessOutput(output)
        logProcessExitCode(output.exitCode)
        return output
    }
}