package jetbrains.buildServer.agent.terraformRunner.cmd.commands

import jetbrains.buildServer.agent.BuildRunnerContext
import jetbrains.buildServer.agent.terraformRunner.cmd.CommandLineBuilder
import jetbrains.buildServer.runner.terraform.TerraformCommandType
import jetbrains.buildServer.runner.terraform.TerraformRunnerInstanceConfiguration

class InitCommandExecution(
        buildRunnerContext: BuildRunnerContext,
        flowId: String
) : TerraformCommandExecution(buildRunnerContext, flowId) {
        override fun prepareArguments(
                config: TerraformRunnerInstanceConfiguration,
                builder: CommandLineBuilder
        ): CommandLineBuilder {
                builder.addArgument(value = TerraformCommandType.INIT.id)
                return builder
        }
}