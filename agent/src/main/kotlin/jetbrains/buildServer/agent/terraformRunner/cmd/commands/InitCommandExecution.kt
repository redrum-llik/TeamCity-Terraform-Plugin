package jetbrains.buildServer.agent.terraformRunner.cmd.commands

import jetbrains.buildServer.agent.BuildRunnerContext

class InitCommandExecution(
        buildRunnerContext: BuildRunnerContext,
        flowId: String
) : TerraformCommandExecution(buildRunnerContext, flowId) {
    // TO-DO? workspace logic
}