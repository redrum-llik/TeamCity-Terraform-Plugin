package jetbrains.buildServer.terraformSupportPlugin.cmd.commands.tfenv;

import jetbrains.buildServer.agent.BuildRunnerContext
import jetbrains.buildServer.agent.runner.ProgramCommandLine
import jetbrains.buildServer.agent.terraformRunner.TerraformCommandLineConstants
import jetbrains.buildServer.agent.terraformRunner.cmd.CommandLineBuilder
import jetbrains.buildServer.agent.terraformRunner.cmd.commands.BaseCommandExecution
import jetbrains.buildServer.terraformSupportPlugin.TerraformRunnerConstants
import jetbrains.buildServer.terraformSupportPlugin.TerraformRunnerInstanceConfiguration
import java.io.File

abstract class BaseTFEnvCommandExecution(
        buildRunnerContext: BuildRunnerContext,
        flowId: String
) : BaseCommandExecution(buildRunnerContext, flowId) {
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