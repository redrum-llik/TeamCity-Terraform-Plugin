package jetbrains.buildServer.agent.terraformRunner.cmd.commands.tfenv;

import jetbrains.buildServer.agent.BuildRunnerContext
import jetbrains.buildServer.agent.runner.ProgramCommandLine
import jetbrains.buildServer.agent.terraformRunner.TerraformCommandLineConstants
import jetbrains.buildServer.agent.terraformRunner.cmd.CommandLineBuilder
import jetbrains.buildServer.agent.terraformRunner.cmd.commands.TerraformCommandExecution
import jetbrains.buildServer.runner.terraform.TerraformRunnerConstants
import jetbrains.buildServer.runner.terraform.TerraformRunnerInstanceConfiguration
import java.io.File

abstract class TFEnvCommandExecution(
        buildRunnerContext: BuildRunnerContext,
        flowId: String
) : TerraformCommandExecution(buildRunnerContext, flowId) {
    override fun getExecutablePath(): String {
        if (buildRunnerContext.isVirtualContext) {
            return TerraformCommandLineConstants.COMMAND_TFENV
        }
        return File(
            buildRunnerContext.configParameters.getOrDefault(
                TerraformRunnerConstants.AGENT_PARAM_TFENV_PATH,
                ""
            ),
            TerraformCommandLineConstants.COMMAND_TERRAFORM
        ).absolutePath
    }

    abstract fun prepareCommandArguments(
            config: TerraformRunnerInstanceConfiguration,
            builder: CommandLineBuilder
    ): CommandLineBuilder

    override fun makeProgramCommandLine(): ProgramCommandLine {
        val builder = CommandLineBuilder()
        val config = TerraformRunnerInstanceConfiguration(buildRunnerContext.runnerParameters)

        builder.executablePath = getExecutablePath()
        builder.workingDir = buildRunnerContext.workingDirectory.path
        prepareCommandArguments(config, builder)

        return builder.build()
    }


}