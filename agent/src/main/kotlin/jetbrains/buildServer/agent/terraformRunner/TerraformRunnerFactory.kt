package jetbrains.buildServer.agent.terraformRunner

import jetbrains.buildServer.agent.AgentBuildRunnerInfo
import jetbrains.buildServer.agent.BuildAgentConfiguration
import jetbrains.buildServer.agent.BuildRunnerContext
import jetbrains.buildServer.agent.runner.CommandExecution
import jetbrains.buildServer.agent.runner.MultiCommandBuildSessionFactory
import jetbrains.buildServer.agent.terraformRunner.cmd.TerraformBuildService
import jetbrains.buildServer.agent.terraformRunner.cmd.commands.ApplyCommandExecution
import jetbrains.buildServer.agent.terraformRunner.cmd.commands.InitCommandExecution
import jetbrains.buildServer.agent.terraformRunner.cmd.commands.PlanCommandExecution
import jetbrains.buildServer.runner.terraform.TerraformCommandType
import jetbrains.buildServer.runner.terraform.TerraformRunnerConstants
import jetbrains.buildServer.runner.terraform.TerraformRunnerInstanceConfiguration

class TerraformRunnerFactory : MultiCommandBuildSessionFactory {
    override fun createSession(runnerContext: BuildRunnerContext): TerraformBuildService {
        val config = TerraformRunnerInstanceConfiguration(runnerContext.runnerParameters)
        return when {
            config.getCommand() == TerraformCommandType.Apply -> {
                object : TerraformBuildService(runnerContext) {
                    override fun instantiateCommands(): List<CommandExecution> {
                        return arrayListOf(
                                ApplyCommandExecution(buildRunnerContext, myFlowId)
                        )
                    }
                }
            }
            config.getCommand() == TerraformCommandType.Init -> {
                object : TerraformBuildService(runnerContext) {
                    override fun instantiateCommands(): List<CommandExecution> {
                        return arrayListOf(
                                InitCommandExecution(buildRunnerContext, myFlowId)
                        )
                    }
                }
            }
            config.getCommand() == TerraformCommandType.Plan -> {
                object : TerraformBuildService(runnerContext) {
                    override fun instantiateCommands(): List<CommandExecution> {
                        if (config.getPlanDoInit()) {
                            return arrayListOf(
                                    InitCommandExecution(buildRunnerContext, myFlowId),
                                    PlanCommandExecution(buildRunnerContext, myFlowId)
                            )
                        }
                        return arrayListOf(
                                PlanCommandExecution(buildRunnerContext, myFlowId)
                        )
                    }
                }
            }
            else -> throw IllegalStateException("No matching build service found for the specified command")
        }
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