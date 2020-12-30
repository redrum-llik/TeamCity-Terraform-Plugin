package jetbrains.buildServer.agent.terraformRunner

import jetbrains.buildServer.agent.AgentBuildRunnerInfo
import jetbrains.buildServer.agent.BuildAgentConfiguration
import jetbrains.buildServer.agent.runner.CommandLineBuildService
import jetbrains.buildServer.agent.runner.CommandLineBuildServiceFactory
import jetbrains.buildServer.runner.terraform.TerraformRunnerConstants

class TerraformPlanRunnerCommandFactory : CommandLineBuildServiceFactory {
    override fun createService(): CommandLineBuildService {
        return TerraformCommandBuildService()
    }

    override fun getBuildRunnerInfo(): AgentBuildRunnerInfo {
        return TerraformPlanRunner()
    }

    companion object {
        class TerraformPlanRunner : AgentBuildRunnerInfo {
            override fun getType(): String {
                return TerraformRunnerConstants.RUNNER_PLAN_TYPE
            }

            override fun canRun(buildAgentConfiguration: BuildAgentConfiguration): Boolean {
                return true
            }
        }
    }
}