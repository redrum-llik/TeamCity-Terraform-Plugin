package jetbrains.buildServer.agent.terraformRunner.cmd.commands.initialization

import jetbrains.buildServer.agent.BuildRunnerContext
import jetbrains.buildServer.agent.terraformRunner.cmd.CommandLineBuilder
import jetbrains.buildServer.agent.terraformRunner.cmd.commands.BaseCommandExecution
import jetbrains.buildServer.runner.terraform.TerraformRunnerInstanceConfiguration
import java.util.regex.Pattern

open class BaseInitializationCommandExecution(
    buildRunnerContext: BuildRunnerContext,
    flowId: String
) : BaseCommandExecution(buildRunnerContext, flowId) {
    protected var processErrorOutput = mutableListOf<String>()

    override fun onErrorOutput(text: String) {
        text.lines().forEach {
            processErrorOutput.add(it)
            myLogger.error(it)
        }
    }

    protected fun findInErrorOutput(pattern: Regex): MatchResult? {
        processErrorOutput.forEach {
            val result = pattern.find(it)
            if (result != null) return result
        }
        return null
    }

    override fun prepareCommonArguments(
        config: TerraformRunnerInstanceConfiguration,
        builder: CommandLineBuilder
    ): CommandLineBuilder = builder
}