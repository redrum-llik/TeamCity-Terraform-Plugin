package jetbrains.buildServer.agent.terraformRunner.cmd.plan

import jetbrains.buildServer.agent.BuildRunnerContext
import jetbrains.buildServer.agent.runner.CommandExecution
import jetbrains.buildServer.agent.terraformRunner.cmd.CommandLineBuilder
import jetbrains.buildServer.agent.terraformRunner.cmd.TerraformBuildService
import jetbrains.buildServer.agent.terraformRunner.cmd.TerraformCommandExecution
import jetbrains.buildServer.agent.terraformRunner.TerraformCommandLineConstants as RunnerConst
import jetbrains.buildServer.runner.terraform.TerraformRunnerInstanceConfiguration

class TerraformPlanBuildService(
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

    override fun prepareArguments(
            config: TerraformRunnerInstanceConfiguration,
            builder: CommandLineBuilder
    ): CommandLineBuilder {
        val customOut = config.getPlanCustomOut()
        if (!customOut.isNullOrEmpty()) {
            builder.addArgument(RunnerConst.PARAM_CUSTOM_OUT, customOut)
        }

        val doDestroy = config.getPlanDoDestroy()
        if (doDestroy) {
            builder.addArgument(RunnerConst.PARAM_DESTROY)
        }

        return builder
    }

    override val myCommandIterator = myCommands.iterator()
}
