package jetbrains.buildServer.agent.terraformRunner.cmd

import com.intellij.openapi.diagnostic.Logger
import jetbrains.buildServer.agent.BuildFinishedStatus
import jetbrains.buildServer.agent.BuildRunnerContext
import jetbrains.buildServer.agent.FlowGenerator
import jetbrains.buildServer.agent.runner.*
import jetbrains.buildServer.agent.terraformRunner.TerraformCommandLineConstants as RunnerConst
import jetbrains.buildServer.runner.terraform.TerraformRunnerInstanceConfiguration

abstract class TerraformBuildService(
        private val buildRunnerContext: BuildRunnerContext,
        private val config: TerraformRunnerInstanceConfiguration
) : MultiCommandBuildSession {
    protected val myFlowId: String = FlowGenerator.generateNewFlow()
    protected abstract val myCommandIterator: Iterator<CommandExecution>

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
            builder.addArgument(RunnerConst.PARAM_NO_COLOR)
        }

        return builder
    }

    open fun makeMainCommandLine(): ProgramCommandLine {
        val builder = CommandLineBuilder()
        val config = TerraformRunnerInstanceConfiguration(buildRunnerContext.runnerParameters)

        builder.executablePath = RunnerConst.COMMAND_TERRAFORM //#FIXME: correct path to executable
        builder.workingDir = buildRunnerContext.workingDirectory.path
        prepareCommandArgument(config, builder)
        prepareArguments(config, builder)
        prepareCommonArguments(config, builder)

        return builder.build()
    }

    override fun getNextCommand(): CommandExecution? {
        return when {
            myCommandIterator.hasNext() -> {
                myCommandIterator.next()
            }
            else -> null
        }
    }

    override fun sessionStarted() {
    }

    override fun sessionFinished(): BuildFinishedStatus? {
        TODO("Not yet implemented")
    }
}

