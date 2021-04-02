package jetbrains.buildServer.agent.terraformRunner.cmd.commands

import jetbrains.buildServer.agent.BuildRunnerContext
import jetbrains.buildServer.agent.terraformRunner.cmd.CommandLineBuilder
import jetbrains.buildServer.runner.terraform.TerraformCommandType
import jetbrains.buildServer.runner.terraform.TerraformRunnerInstanceConfiguration

class CustomCommandExecution(
        buildRunnerContext: BuildRunnerContext,
        flowId: String,
        private val customCommand: String
) : TerraformCommandExecution(buildRunnerContext, flowId) {
        override fun prepareArguments(
                config: TerraformRunnerInstanceConfiguration,
                builder: CommandLineBuilder
        ): CommandLineBuilder {
                builder.addArgument(value = customCommand)
                return builder
        }
}