package jetbrains.buildServer.agent.terraformRunner.cmd.init

import jetbrains.buildServer.agent.BuildRunnerContext
import jetbrains.buildServer.agent.runner.CommandExecution
import jetbrains.buildServer.agent.terraformRunner.cmd.TerraformBuildService
import jetbrains.buildServer.agent.terraformRunner.cmd.TerraformCommandExecution
import jetbrains.buildServer.runner.terraform.TerraformRunnerInstanceConfiguration

class TerraformInitBuildService(
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
    override val myCommandIterator = myCommands.iterator()
}
