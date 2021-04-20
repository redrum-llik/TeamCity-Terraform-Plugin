package jetbrains.buildServer.agent.terraformRunner.cmd.commands.initialization

import jetbrains.buildServer.agent.BuildRunnerContext
import jetbrains.buildServer.agent.terraformRunner.cmd.CommandLineBuilder
import jetbrains.buildServer.runner.terraform.TerraformCommandType
import jetbrains.buildServer.runner.terraform.TerraformRunnerInstanceConfiguration
import jetbrains.buildServer.agent.terraformRunner.TerraformCommandLineConstants as RunnerConst

class WorkspaceSelectCommandExecution(
    buildRunnerContext: BuildRunnerContext,
    flowId: String,
    private val workspaceName: String
) : BaseInitializationCommandExecution(buildRunnerContext, flowId) {
    private val pattern = "Workspace \".*\" doesn't exist\\.".toRegex()

    override fun describe(): String = "terraform workspace select"

    override fun prepareArguments(
        config: TerraformRunnerInstanceConfiguration,
        builder: CommandLineBuilder
    ): CommandLineBuilder {
        builder.addArgument(argName = TerraformCommandType.WORKSPACE.id)
        builder.addArgument(argName = RunnerConst.PARAM_COMMAND_SELECT)
        builder.addArgument(value = workspaceName)
        return builder
    }

    override fun processFinished(exitCode: Int) {
        myLogger.message("##teamcity[blockClosed name='${describe()}']")
        val config = TerraformRunnerInstanceConfiguration(buildRunnerContext.runnerParameters)
        myLogger.apply {
            if (exitCode != 0) {
                val result = findInErrorOutput(pattern)
                if (!config.getDoCreateWorkspaceIfNotFound() && result == null) {
                    myHasProblem = true
                    error("Command failed with code $exitCode")
                }
            }
        }
    }
}