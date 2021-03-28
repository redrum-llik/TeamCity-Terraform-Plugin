package jetbrains.buildServer.agent.terraformRunner.cmd.commands;

import jetbrains.buildServer.agent.BuildRunnerContext
import jetbrains.buildServer.agent.runner.ProgramCommandLine
import jetbrains.buildServer.agent.terraformRunner.TerraformCommandLineConstants
import jetbrains.buildServer.agent.terraformRunner.cmd.CommandLineBuilder
import jetbrains.buildServer.runner.terraform.TerraformRunnerInstanceConfiguration

abstract class TFEnvCommandExecution(
        buildRunnerContext: BuildRunnerContext,
        flowId: String
) : TerraformCommandExecution(buildRunnerContext, flowId) {
    abstract fun prepareCommandArguments(
            config: TerraformRunnerInstanceConfiguration,
            builder: CommandLineBuilder
    ): CommandLineBuilder

    override fun makeProgramCommandLine(): ProgramCommandLine {
        val builder = CommandLineBuilder()
        val config = TerraformRunnerInstanceConfiguration(buildRunnerContext.runnerParameters)

        builder.executablePath = TerraformCommandLineConstants.COMMAND_TFENV //#FIXME: correct path to executable
        builder.workingDir = buildRunnerContext.workingDirectory.path
        prepareCommandArguments(config, builder)

        return builder.build()
    }


}