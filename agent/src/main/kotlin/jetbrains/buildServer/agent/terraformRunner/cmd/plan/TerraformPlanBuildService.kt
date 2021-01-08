package jetbrains.buildServer.agent.terraformRunner.cmd.plan

import jetbrains.buildServer.agent.BuildFinishedStatus
import jetbrains.buildServer.agent.BuildRunnerContext
import jetbrains.buildServer.agent.runner.CommandExecution
import jetbrains.buildServer.agent.runner.ProgramCommandLine
import jetbrains.buildServer.agent.terraformRunner.cmd.TerraformBuildService
import jetbrains.buildServer.runner.terraform.TerraformRunnerInstanceConfiguration

class TerraformPlanBuildService(
        buildRunnerContext: BuildRunnerContext,
        config: TerraformRunnerInstanceConfiguration
) : TerraformBuildService(buildRunnerContext, config) {
    private val myCommands: ArrayList<CommandExecution> = arrayListOf(
            PlanCommandExecution(
                    makeCommandLine(),
                    buildRunnerContext,
                    myFlowId
            )
    )
    override val myCommandIterator = myCommands.iterator()

    override fun makeCommandLine(): ProgramCommandLine {
        TODO("Not yet implemented")
    }
}
