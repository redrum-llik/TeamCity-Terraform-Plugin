package jetbrains.buildServer.agent.terraformRunner.cmd.commands.workspace

import jetbrains.buildServer.agent.BuildRunnerContext
import jetbrains.buildServer.agent.terraformRunner.cmd.CommandLineBuilder
import jetbrains.buildServer.agent.terraformRunner.cmd.commands.TerraformCommandExecution
import jetbrains.buildServer.runner.terraform.TerraformCommandType
import jetbrains.buildServer.runner.terraform.TerraformRunnerInstanceConfiguration

class WorkspaceSelectCommandExecution(
    buildRunnerContext: BuildRunnerContext,
    flowId: String,
    private val workspaceName: String
) : TerraformCommandExecution(buildRunnerContext, flowId) {
    override fun prepareArguments(
        config: TerraformRunnerInstanceConfiguration,
        builder: CommandLineBuilder
    ): CommandLineBuilder {
        builder.addArgument(value = TerraformCommandType.WORKSPACE_SELECT.id)
        builder.addArgument(value = workspaceName)
        return builder
    }

    override fun processFinished(exitCode: Int) {
        myLogger.message("##teamcity[blockClosed name='$myCommandLineTruncated']")
        val config = TerraformRunnerInstanceConfiguration(buildRunnerContext.runnerParameters)
        myLogger.apply {
            if (exitCode != 0) {
                if (!config.getDoCreateWorkspaceIfNotFound()) {
                    myHasProblem = true
                    error("Command failed with code $exitCode")
                }
            }
        }
    }
}