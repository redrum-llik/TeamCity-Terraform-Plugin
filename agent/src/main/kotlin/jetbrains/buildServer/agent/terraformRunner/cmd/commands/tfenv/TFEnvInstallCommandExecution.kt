package jetbrains.buildServer.agent.terraformRunner.cmd.commands.tfenv

import jetbrains.buildServer.agent.BuildRunnerContext
import jetbrains.buildServer.runner.terraform.TerraformCommandLineConstants as RunnerConst
import jetbrains.buildServer.agent.terraformRunner.cmd.CommandLineBuilder
import jetbrains.buildServer.runner.terraform.TerraformRunnerInstanceConfiguration

class TFEnvInstallCommandExecution(
        buildRunnerContext: BuildRunnerContext,
        flowId: String
) : BaseTFEnvCommandExecution(buildRunnerContext, flowId) {
    override fun describe(): String = "tfenv install"

    override fun prepareCommandArguments(
            config: TerraformRunnerInstanceConfiguration,
            builder: CommandLineBuilder
    ): CommandLineBuilder {
        val version = config.getTFEnvVersion()
        builder.addArgument(value = RunnerConst.PARAM_COMMAND_INSTALL)
        if (version.isNullOrBlank()) {
            myLogger.debug("No target version specified, tfenv will try to locate version reference in the state files")
        }
        else {
            builder.addArgument(value = version)
        }

        return builder
    }
}