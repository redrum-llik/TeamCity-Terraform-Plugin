package jetbrains.buildServer.agent.terraformRunner

import jetbrains.buildServer.agent.AgentBuildRunnerInfo
import jetbrains.buildServer.agent.BuildAgentConfiguration
import jetbrains.buildServer.agent.runner.CommandLineBuildService
import jetbrains.buildServer.agent.runner.CommandLineBuildServiceFactory
import jetbrains.buildServer.runner.terraform.TerraformRunnerConstants

class TerraformRunnerCommandLineServiceFactory : CommandLineBuildServiceFactory {
    override fun createService(): CommandLineBuildService {
        return TerraformCommandBuildService()
    }

    override fun getBuildRunnerInfo(): AgentBuildRunnerInfo {
        return AgentTerraformRunnerInfo()
    }

    companion object {
        class AgentTerraformRunnerInfo : AgentBuildRunnerInfo {
            override fun getType(): String {
                return TerraformRunnerConstants.RUNNER_TYPE
            }

            override fun canRun(buildAgentConfiguration: BuildAgentConfiguration): Boolean {
                return true
            }
        }
    }
}