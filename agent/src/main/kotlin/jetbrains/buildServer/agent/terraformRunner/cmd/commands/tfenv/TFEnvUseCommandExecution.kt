package jetbrains.buildServer.agent.terraformRunner.cmd.commands.tfenv

import jetbrains.buildServer.agent.BuildRunnerContext
import jetbrains.buildServer.runner.terraform.TerraformCommandLineConstants
import jetbrains.buildServer.agent.terraformRunner.cmd.CommandLineBuilder
import jetbrains.buildServer.runner.terraform.TerraformRunnerInstanceConfiguration

class TFEnvUseCommandExecution(
        buildRunnerContext: BuildRunnerContext,
        flowId: String
) : BaseTFEnvCommandExecution(buildRunnerContext, flowId) {
    override fun describe(): String = "tfenv use"

    override fun prepareCommandArguments(
            config: TerraformRunnerInstanceConfiguration,
            builder: CommandLineBuilder
    ): CommandLineBuilder {
        val version = config.getTFEnvVersion()
        builder.addArgument(value = TerraformCommandLineConstants.PARAM_COMMAND_USE)
        if (version.isNullOrBlank()) {
            myLogger.debug("No target version specified, tfenv will try to locate version reference in the state files")
        }
        else {
            builder.addArgument(value = version)
        }

        return builder
    }
}