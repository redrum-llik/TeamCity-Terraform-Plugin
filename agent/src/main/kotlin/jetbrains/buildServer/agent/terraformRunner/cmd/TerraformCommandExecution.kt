package jetbrains.buildServer.agent.terraformRunner.cmd

import jetbrains.buildServer.agent.BuildRunnerContext
import jetbrains.buildServer.agent.runner.CommandExecution
import jetbrains.buildServer.agent.runner.ProgramCommandLine
import jetbrains.buildServer.agent.runner.TerminationAction

import java.io.File

open class TerraformCommandExecution(
        private val commandLine: ProgramCommandLine,
        buildRunnerContext: BuildRunnerContext,
        flowId: String
) : CommandExecution {
    private val myLogger = buildRunnerContext.build.buildLogger.getFlowLogger(flowId)

    override fun processStarted(programCommandLine: String, workingDirectory: File) {
        myLogger.message("Starting $programCommandLine, working directory: $workingDirectory")
    }

    override fun onStandardOutput(text: String) {
        text.lines().forEach {
            myLogger.message(it)
        }
    }

    override fun onErrorOutput(text: String) {
        text.lines().forEach {
            myLogger.error(it)
        }
    }

    override fun processFinished(exitCode: Int) {
        myLogger.apply {
            if (exitCode != 0) {
                error("Command failed with code $exitCode")
            } else {
                message("Command successfully exited with code $exitCode")
            }
        }
    }

    override fun interruptRequested(): TerminationAction = TerminationAction.KILL_PROCESS_TREE

    override fun isCommandLineLoggingEnabled(): Boolean = true

    override fun makeProgramCommandLine(): ProgramCommandLine {
        return commandLine
    }

    override fun beforeProcessStarted() {
    }
}