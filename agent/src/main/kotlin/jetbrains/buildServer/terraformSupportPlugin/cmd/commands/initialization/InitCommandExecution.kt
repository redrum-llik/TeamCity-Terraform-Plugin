package jetbrains.buildServer.terraformSupportPlugin.cmd.commands.initialization

import jetbrains.buildServer.agent.BuildRunnerContext
import jetbrains.buildServer.agent.terraformRunner.cmd.CommandLineBuilder
import jetbrains.buildServer.terraformSupportPlugin.TerraformCommandType
import jetbrains.buildServer.terraformSupportPlugin.TerraformRunnerInstanceConfiguration

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