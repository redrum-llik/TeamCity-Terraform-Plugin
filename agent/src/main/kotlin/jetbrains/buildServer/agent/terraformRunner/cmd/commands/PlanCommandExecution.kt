package jetbrains.buildServer.agent.terraformRunner.cmd.commands

import jetbrains.buildServer.agent.BuildRunnerContext
import jetbrains.buildServer.agent.terraformRunner.TerraformCommandLineConstants
import jetbrains.buildServer.agent.terraformRunner.cmd.CommandLineBuilder
import jetbrains.buildServer.runner.terraform.TerraformCommandType
import jetbrains.buildServer.runner.terraform.TerraformRunnerInstanceConfiguration
import java.io.File

class PlanCommandExecution(
    buildRunnerContext: BuildRunnerContext,
    flowId: String
) : BaseCommandExecution(buildRunnerContext, flowId) {
    private val outPattern = "-out\\s(\\S*)\\s".toRegex()
    private val customOut = getPlanOutputPath()

    override fun describe(): String = "terraform plan"

    private fun getPlanOutputPath(): OutputPath {
        val config = TerraformRunnerInstanceConfiguration(buildRunnerContext.runnerParameters)

        val planCustomOut = config.getPlanCustomOut()
        if (!planCustomOut.isNullOrEmpty()) {
            return OutputPath(planCustomOut, true)
        }

        val extraArgs = config.getExtraArgs()
        if (!extraArgs.isNullOrEmpty() &&
            extraArgs.contains(TerraformCommandLineConstants.PARAM_CUSTOM_OUT)
        ) {
            val outPath = outPattern.find(extraArgs)
            if (outPath != null) {
                return OutputPath(outPath.groupValues[0], false)
            }
        }

        val workspaceName = config.getUseWorkspace()
        if (!workspaceName.isNullOrEmpty()) {
            return OutputPath("terraform_plan_$workspaceName.out", true)
        }

        return OutputPath("terraform_plan.out", true)
    }

    private fun publishPlanOutput(path: String) {
        val basePath = buildRunnerContext.build.checkoutDirectory
        val relativePath = File(
            buildRunnerContext.workingDirectory,
            path
        ).relativeTo(basePath).path
        val rule = "+:$relativePath"
        myLogger.message("Publishing 'terraform plan' output with the following rule: '$rule'")
        myLogger.message("##teamcity[publishArtifacts '$rule']") //#FIXME see other service messages
    }

    override fun prepareArguments(
        config: TerraformRunnerInstanceConfiguration,
        builder: CommandLineBuilder
    ): CommandLineBuilder {
        builder.addArgument(value = TerraformCommandType.PLAN.id)

        if (customOut.needToAppend) {
            builder.addArgument(
                TerraformCommandLineConstants.PARAM_CUSTOM_OUT,
                customOut.path
            )
        }

        if (checkTerraformPrefixedSystemParameters()) {
            preparePrefixedSystemParametersAsArguments(builder)
        }

        return builder
    }

    override fun processFinished(exitCode: Int) {
        super.processFinished(exitCode)
        publishPlanOutput(customOut.path)
    }

    companion object {
        data class OutputPath(
            val path: String,
            val needToAppend: Boolean
        )
    }
}