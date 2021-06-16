package jetbrains.buildServer.terraformSupportPlugin

import jetbrains.buildServer.agent.AgentBuildRunnerInfo
import jetbrains.buildServer.agent.BuildAgentConfiguration
import jetbrains.buildServer.agent.BuildRunnerContext
import jetbrains.buildServer.agent.runner.MultiCommandBuildSessionFactory
import jetbrains.buildServer.agent.terraformRunner.cmd.TerraformBuildService
import jetbrains.buildServer.agent.terraformRunner.cmd.commands.*
import jetbrains.buildServer.agent.terraformRunner.cmd.commands.initialization.InitCommandExecution
import jetbrains.buildServer.agent.terraformRunner.cmd.commands.tfenv.TFEnvInstallCommandExecution
import jetbrains.buildServer.agent.terraformRunner.cmd.commands.tfenv.TFEnvUseCommandExecution
import jetbrains.buildServer.agent.terraformRunner.cmd.commands.initialization.WorkspaceNewCommandExecution
import jetbrains.buildServer.agent.terraformRunner.cmd.commands.initialization.WorkspaceSelectCommandExecution
import jetbrains.buildServer.terraformSupportPlugin.TerraformCommandType
import jetbrains.buildServer.terraformSupportPlugin.TerraformRunnerConstants
import jetbrains.buildServer.terraformSupportPlugin.TerraformRunnerInstanceConfiguration
import jetbrains.buildServer.terraformSupportPlugin.TerraformVersionMode

class TerraformRunnerFactory : MultiCommandBuildSessionFactory {
    override fun createSession(runnerContext: BuildRunnerContext): TerraformBuildService {
        val config = TerraformRunnerInstanceConfiguration(runnerContext.runnerParameters)
        return when {
            config.getCommand() == TerraformCommandType.APPLY -> {
                object : TerraformBuildService(runnerContext) {
                    override fun instantiateCommands(): List<BaseCommandExecution> {
                        return instantiateApplyCommands(runnerContext, myFlowId)
                    }
                }
            }
            config.getCommand() == TerraformCommandType.PLAN -> {
                object : TerraformBuildService(runnerContext) {
                    override fun instantiateCommands(): List<BaseCommandExecution> {
                        return instantiatePlanCommands(runnerContext, myFlowId)
                    }
                }
            }
            config.getCommand() == TerraformCommandType.CUSTOM -> {
                object : TerraformBuildService(runnerContext) {
                    override fun instantiateCommands(): List<BaseCommandExecution> {
                        return instantiateCustomCommands(runnerContext, myFlowId)
                    }
                }
            }
            else -> throw IllegalStateException("No matching build service found for the specified command")
        }
    }

    private fun instantiateTFEnvCommands(
        buildRunnerContext: BuildRunnerContext,
        flowId: String
    ): ArrayList<BaseCommandExecution> {
        val config = TerraformRunnerInstanceConfiguration(buildRunnerContext.runnerParameters)
        if (config.getVersionMode() == TerraformVersionMode.TFENV) {
            return arrayListOf(
                TFEnvInstallCommandExecution(buildRunnerContext, flowId),
                TFEnvUseCommandExecution(buildRunnerContext, flowId)
            )
        }
        return ArrayList()
    }

    private fun instantiateInitStageCommands(
        buildRunnerContext: BuildRunnerContext,
        flowId: String
    ): ArrayList<BaseCommandExecution> {
        val commands: ArrayList<BaseCommandExecution> = ArrayList()
        val config = TerraformRunnerInstanceConfiguration(buildRunnerContext.runnerParameters)
        val workspaceName = config.getUseWorkspace()

        if (config.getVersionMode() == TerraformVersionMode.TFENV) {
            commands.addAll(
                instantiateTFEnvCommands(buildRunnerContext, flowId)
            )
        }

        if (!workspaceName.isNullOrEmpty()) {
            if (config.getDoCreateWorkspaceIfNotFound()) {
                commands.addAll(
                    arrayListOf(
                        WorkspaceSelectCommandExecution(buildRunnerContext, flowId, workspaceName),
                        WorkspaceNewCommandExecution(buildRunnerContext, flowId, workspaceName)
                    )
                )
            } else {
                commands.add(
                    WorkspaceSelectCommandExecution(buildRunnerContext, flowId, workspaceName)
                )
            }
        }

        if (config.getDoInit()) {
            commands.add(
                InitCommandExecution(buildRunnerContext, flowId)
            )
        }

        return commands
    }


    fun instantiateCustomCommands(
        buildRunnerContext: BuildRunnerContext,
        flowId: String
    ): List<BaseCommandExecution> {
        val config = TerraformRunnerInstanceConfiguration(buildRunnerContext.runnerParameters)
        val commands: ArrayList<BaseCommandExecution> = ArrayList()
        commands.addAll(instantiateInitStageCommands(buildRunnerContext, flowId))
        commands.add(
            CustomCommandExecution(buildRunnerContext, flowId, config.getCustomCommand()!!)
        )
        return commands
    }

    fun instantiateApplyCommands(
        buildRunnerContext: BuildRunnerContext,
        flowId: String
    ): List<BaseCommandExecution> {
        val commands: ArrayList<BaseCommandExecution> = ArrayList()
        commands.addAll(instantiateInitStageCommands(buildRunnerContext, flowId))
        commands.add(
            ApplyCommandExecution(buildRunnerContext, flowId)
        )
        return commands
    }

    fun instantiatePlanCommands(
        buildRunnerContext: BuildRunnerContext,
        flowId: String
    ): List<BaseCommandExecution> {
        val commands: ArrayList<BaseCommandExecution> = ArrayList()
        commands.addAll(instantiateInitStageCommands(buildRunnerContext, flowId))
        commands.add(
            PlanCommandExecution(buildRunnerContext, flowId)
        )
        return commands
    }

    override fun getBuildRunnerInfo(): AgentBuildRunnerInfo {
        return TerraformRunner()
    }

    companion object {
        class TerraformRunner : AgentBuildRunnerInfo {
            override fun getType(): String {
                return TerraformRunnerConstants.RUNNER_TYPE
            }

            override fun canRun(buildAgentConfiguration: BuildAgentConfiguration): Boolean {
                return true
            }
        }
    }
}