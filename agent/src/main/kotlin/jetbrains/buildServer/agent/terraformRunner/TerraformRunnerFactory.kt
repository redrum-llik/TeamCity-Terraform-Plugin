package jetbrains.buildServer.agent.terraformRunner

import jetbrains.buildServer.agent.AgentBuildRunnerInfo
import jetbrains.buildServer.agent.BuildAgentConfiguration
import jetbrains.buildServer.agent.BuildRunnerContext
import jetbrains.buildServer.agent.runner.MultiCommandBuildSessionFactory
import jetbrains.buildServer.agent.terraformRunner.cmd.TerraformBuildService
import jetbrains.buildServer.agent.terraformRunner.cmd.apply.TerraformApplyBuildService
import jetbrains.buildServer.agent.terraformRunner.cmd.init.TerraformInitBuildService
import jetbrains.buildServer.agent.terraformRunner.cmd.plan.TerraformPlanBuildService
import jetbrains.buildServer.runner.terraform.TerraformCommandType
import jetbrains.buildServer.runner.terraform.TerraformRunnerConstants
import jetbrains.buildServer.runner.terraform.TerraformRunnerInstanceConfiguration

class TerraformRunnerFactory : MultiCommandBuildSessionFactory {
    override fun createSession(runnerContext: BuildRunnerContext): TerraformBuildService {
        val config = TerraformRunnerInstanceConfiguration(runnerContext.runnerParameters)
        return when {
            config.getCommand() == TerraformCommandType.Apply -> {
                TerraformApplyBuildService(runnerContext, config)
            }
            config.getCommand() == TerraformCommandType.Init -> {
                TerraformInitBuildService(runnerContext, config)
            }
            config.getCommand() == TerraformCommandType.Plan -> {
                TerraformPlanBuildService(runnerContext, config)
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