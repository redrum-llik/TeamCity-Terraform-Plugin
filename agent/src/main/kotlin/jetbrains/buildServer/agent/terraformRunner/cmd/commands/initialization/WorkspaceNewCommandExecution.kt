package jetbrains.buildServer.agent.terraformRunner.cmd.commands.initialization

import jetbrains.buildServer.agent.BuildRunnerContext
import jetbrains.buildServer.agent.terraformRunner.TerraformCommandLineConstants
import jetbrains.buildServer.agent.terraformRunner.cmd.CommandLineBuilder
import jetbrains.buildServer.runner.terraform.TerraformCommandType
import jetbrains.buildServer.runner.terraform.TerraformRunnerInstanceConfiguration

class WorkspaceNewCommandExecution(
    buildRunnerContext: BuildRunnerContext,
    flowId: String,
    private val workspaceName: String
) : BaseInitializationCommandExecution(buildRunnerContext, flowId) {
    val pattern = "Workspace \".*\" already exists".toRegex()

    override fun prepareArguments(
        config: TerraformRunnerInstanceConfiguration,
        builder: CommandLineBuilder
    ): CommandLineBuilder {
        builder.addArgument(argName = TerraformCommandType.WORKSPACE.id)
        builder.addArgument(argName = TerraformCommandLineConstants.PARAM_COMMAND_NEW)
        builder.addArgument(value = workspaceName)
        return builder
    }

    override fun processFinished(exitCode: Int) {
        myLogger.message("##teamcity[blockClosed name='$myCommandLineTruncated']")
        val config = TerraformRunnerInstanceConfiguration(buildRunnerContext.runnerParameters)
        myLogger.apply {
            if (exitCode != 0) {
                val result = findInErrorOutput(pattern)
                if (result == null) {
                    myHasProblem = true
                    error("Command failed with code $exitCode")
                }
            }
        }
    }
}