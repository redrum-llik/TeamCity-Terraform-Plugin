package jetbrains.buildServer.agent.terraformRunner.cmd.commands

import jetbrains.buildServer.agent.BuildRunnerContext
import jetbrains.buildServer.agent.terraformRunner.TerraformCommandLineConstants
import jetbrains.buildServer.agent.terraformRunner.cmd.CommandLineBuilder
import jetbrains.buildServer.runner.terraform.TerraformCommandType
import jetbrains.buildServer.runner.terraform.TerraformRunnerInstanceConfiguration

class ApplyCommandExecution(
        buildRunnerContext: BuildRunnerContext,
        flowId: String
) : BaseCommandExecution(buildRunnerContext, flowId) {
    override fun describe(): String = "terraform apply"

    override fun prepareArguments(
            config: TerraformRunnerInstanceConfiguration,
            builder: CommandLineBuilder
    ): CommandLineBuilder {
        builder.addArgument(value = TerraformCommandType.APPLY.id)

        val customBackupOut = config.getApplyCustomBackupOut()
        if (!customBackupOut.isNullOrEmpty()) {
            builder.addArgument(TerraformCommandLineConstants.PARAM_CUSTOM_BACKUP_OUT)
        }

        builder.addArgument(TerraformCommandLineConstants.PARAM_AUTO_APPROVE)

        return builder
    }
}