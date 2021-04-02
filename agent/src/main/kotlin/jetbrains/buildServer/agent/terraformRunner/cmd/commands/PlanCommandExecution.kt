package jetbrains.buildServer.agent.terraformRunner.cmd.commands

import jetbrains.buildServer.agent.BuildRunnerContext
import jetbrains.buildServer.agent.terraformRunner.TerraformCommandLineConstants
import jetbrains.buildServer.agent.terraformRunner.cmd.CommandLineBuilder
import jetbrains.buildServer.runner.terraform.TerraformCommandType
import jetbrains.buildServer.runner.terraform.TerraformRunnerConstants
import jetbrains.buildServer.runner.terraform.TerraformRunnerInstanceConfiguration

class PlanCommandExecution(
        buildRunnerContext: BuildRunnerContext,
        flowId: String
) : TerraformCommandExecution(buildRunnerContext, flowId) {
    private fun storePlanOutputPath(path: String) {
        buildRunnerContext.build.addSharedConfigParameter(
                TerraformRunnerConstants.BUILD_PARAM_OUT_ARTIFACT_PATH,
                path
        )
    }

    override fun prepareArguments(
            config: TerraformRunnerInstanceConfiguration,
            builder: CommandLineBuilder
    ): CommandLineBuilder {
        builder.addArgument(value = TerraformCommandType.PLAN.id)

        val customOut = config.getPlanCustomOut()
        if (!customOut.isNullOrEmpty()) {
            builder.addArgument(TerraformCommandLineConstants.PARAM_CUSTOM_OUT, customOut)
        }

        val outValue = builder.getArgumentValue(TerraformCommandLineConstants.PARAM_CUSTOM_OUT)
        if (!outValue.isNullOrEmpty()) {
            storePlanOutputPath(outValue)
        }

        if (checkTerraformPrefixedSystemParameters()) {
            preparePrefixedSystemParametersAsArguments(builder)
        }

        return builder
    }
}