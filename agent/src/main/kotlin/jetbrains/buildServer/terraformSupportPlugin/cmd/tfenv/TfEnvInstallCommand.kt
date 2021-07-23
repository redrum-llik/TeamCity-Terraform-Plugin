package jetbrains.buildServer.terraformSupportPlugin.cmd.tfenv

import jetbrains.buildServer.agent.AgentRunningBuild
import jetbrains.buildServer.agent.FlowLogger
import jetbrains.buildServer.terraformSupportPlugin.TerraformFeatureConfiguration
import jetbrains.buildServer.terraformSupportPlugin.TerraformRuntimeConstants
import jetbrains.buildServer.terraformSupportPlugin.cmd.CommandLineBuilder

class TfEnvInstallCommand(
    myBuild: AgentRunningBuild,
    myLogger: FlowLogger,
    myConfiguration: TerraformFeatureConfiguration
) : BaseTfEnvCommand(myBuild, myLogger, myConfiguration) {
    override fun prepareArguments(builder: CommandLineBuilder): CommandLineBuilder {
        builder.addArgument(value = TerraformRuntimeConstants.PARAM_COMMAND_INSTALL)
        handleTerraformVersionParam(builder)

        return builder
    }

    override fun describe(): String {
        return "`tfenv use`"
    }
}