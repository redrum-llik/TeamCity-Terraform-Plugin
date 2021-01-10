package jetbrains.buildServer.agent.terraformRunner.cmd.apply

import jetbrains.buildServer.agent.BuildRunnerContext
import jetbrains.buildServer.agent.runner.CommandExecution
import jetbrains.buildServer.agent.runner.ProgramCommandLine
import jetbrains.buildServer.agent.terraformRunner.cmd.CommandLineBuilder
import jetbrains.buildServer.agent.terraformRunner.cmd.TerraformBuildService
import jetbrains.buildServer.agent.terraformRunner.cmd.TerraformCommandExecution
import jetbrains.buildServer.agent.terraformRunner.TerraformCommandLineConstants as RunnerConst
import jetbrains.buildServer.runner.terraform.TerraformRunnerInstanceConfiguration

class TerraformApplyBuildService(
        buildRunnerContext: BuildRunnerContext,
        config: TerraformRunnerInstanceConfiguration
) : TerraformBuildService(buildRunnerContext, config) {
    private val myCommands: ArrayList<CommandExecution> = arrayListOf(
            TerraformCommandExecution(
                    makeMainCommandLine(),
                    buildRunnerContext,
                    myFlowId
            )
    )

    override fun prepareArguments(
            config: TerraformRunnerInstanceConfiguration,
            builder: CommandLineBuilder
    ): CommandLineBuilder {
        val customBackupOut = config.getApplyCustomBackupOut()
        if (!customBackupOut.isNullOrEmpty()) {
            builder.addArgument(RunnerConst.PARAM_CUSTOM_BACKUP_OUT)
        }

        val doAutoApprove = config.getApplyDoAutoApprove()
        if (doAutoApprove) {
            builder.addArgument(RunnerConst.PARAM_AUTO_APPROVE)
        }

        return builder
    }

    override val myCommandIterator = myCommands.iterator()
}
