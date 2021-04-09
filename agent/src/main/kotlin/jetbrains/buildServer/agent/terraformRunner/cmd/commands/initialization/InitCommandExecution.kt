package jetbrains.buildServer.agent.terraformRunner.cmd.commands.initialization

import jetbrains.buildServer.agent.BuildRunnerContext
import jetbrains.buildServer.agent.terraformRunner.cmd.CommandLineBuilder
import jetbrains.buildServer.runner.terraform.TerraformCommandType
import jetbrains.buildServer.runner.terraform.TerraformRunnerInstanceConfiguration

class InitCommandExecution(
    buildRunnerContext: BuildRunnerContext,
    flowId: String
) : BaseInitializationCommandExecution(buildRunnerContext, flowId) {
    override fun describe(): String = "terraform init"

    override fun prepareArguments(
        config: TerraformRunnerInstanceConfiguration,
        builder: CommandLineBuilder
    ): CommandLineBuilder {
        builder.addArgument(argName = TerraformCommandType.INIT.id)
        return builder
    }
}