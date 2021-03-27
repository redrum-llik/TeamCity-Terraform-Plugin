package jetbrains.buildServer.agent.terraformRunner.cmd.commands

import jetbrains.buildServer.agent.BuildRunnerContext
import jetbrains.buildServer.agent.terraformRunner.TerraformCommandLineConstants
import jetbrains.buildServer.agent.terraformRunner.cmd.CommandLineBuilder
import jetbrains.buildServer.runner.terraform.TerraformCommandType
import jetbrains.buildServer.runner.terraform.TerraformRunnerInstanceConfiguration

class ApplyCommandExecution(
        buildRunnerContext: BuildRunnerContext,
        flowId: String
) : TerraformCommandExecution(buildRunnerContext, flowId) {
    override fun prepareArguments(
            config: TerraformRunnerInstanceConfiguration,
            builder: CommandLineBuilder
    ): CommandLineBuilder {
        builder.addArgument(value = TerraformCommandType.APPLY.id)

        val customBackupOut = config.getApplyCustomBackupOut()
        if (!customBackupOut.isNullOrEmpty()) {
            builder.addArgument(TerraformCommandLineConstants.PARAM_CUSTOM_BACKUP_OUT)
        }

        val doAutoApprove = config.getApplyDoAutoApprove()
        if (doAutoApprove) {
            builder.addArgument(TerraformCommandLineConstants.PARAM_AUTO_APPROVE)
        }

        val doPassConfigParams = config.getDoPassConfigParams()
        if (doPassConfigParams) {
            prepareConfigurationParametersAsArguments(builder)
        }

        return builder
    }
}