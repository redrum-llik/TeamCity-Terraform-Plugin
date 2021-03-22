package jetbrains.buildServer.agent.terraformRunner.cmd.commands

import jetbrains.buildServer.agent.BuildRunnerContext
import jetbrains.buildServer.agent.runner.CommandExecution
import jetbrains.buildServer.agent.runner.ProgramCommandLine
import jetbrains.buildServer.agent.runner.TerminationAction
import jetbrains.buildServer.agent.terraformRunner.TerraformCommandLineConstants
import jetbrains.buildServer.agent.terraformRunner.cmd.CommandLineBuilder
import jetbrains.buildServer.runner.terraform.TerraformRunnerInstanceConfiguration

import java.io.File

open class TerraformCommandExecution(
        val buildRunnerContext: BuildRunnerContext,
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

    protected open fun prepareArguments(
            config: TerraformRunnerInstanceConfiguration,
            builder: CommandLineBuilder
    ): CommandLineBuilder {
        return builder
    }

    private fun prepareCommandArgument(
            config: TerraformRunnerInstanceConfiguration,
            builder: CommandLineBuilder
    ): CommandLineBuilder {
        val command = config.getCommand().id
        builder.addArgument(value = command)

        return builder
    }

    private fun prepareCommonArguments(
            config: TerraformRunnerInstanceConfiguration,
            builder: CommandLineBuilder
    ): CommandLineBuilder {
        val extraArgs = config.getExtraArgs()
        if (!extraArgs.isNullOrEmpty()) {
            builder.addArgument(value = extraArgs)
        }

        val doColor = config.getDoColor()
        if (!doColor) {
            builder.addArgument(TerraformCommandLineConstants.PARAM_NO_COLOR)
        }

        return builder
    }

    override fun makeProgramCommandLine(): ProgramCommandLine {
        val builder = CommandLineBuilder()
        val config = TerraformRunnerInstanceConfiguration(buildRunnerContext.runnerParameters)

        builder.executablePath = TerraformCommandLineConstants.COMMAND_TERRAFORM //#FIXME: correct path to executable
        builder.workingDir = buildRunnerContext.workingDirectory.path
        prepareCommandArgument(config, builder)
        prepareArguments(config, builder)
        prepareCommonArguments(config, builder)

        return builder.build()
    }

    override fun beforeProcessStarted() {
    }
}