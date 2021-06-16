package jetbrains.buildServer.terraformSupportPlugin.cmd.commands

import jetbrains.buildServer.agent.BuildRunnerContext
import jetbrains.buildServer.agent.terraformRunner.cmd.CommandLineBuilder
import jetbrains.buildServer.terraformSupportPlugin.TerraformRunnerInstanceConfiguration

class CustomCommandExecution(
        buildRunnerContext: BuildRunnerContext,
        flowId: String,
        private val customCommand: String
) : BaseCommandExecution(buildRunnerContext, flowId) {
        override fun describe(): String = "terraform $customCommand"

        override fun prepareArguments(
            config: TerraformRunnerInstanceConfiguration,
            builder: CommandLineBuilder
        ): CommandLineBuilder {
                builder.addArgument(value = customCommand)

                return builder
        }
}