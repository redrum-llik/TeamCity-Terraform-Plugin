package jetbrains.buildServer.terraformSupportPlugin.cmd

import jetbrains.buildServer.agent.AgentRunningBuild
import jetbrains.buildServer.agent.FlowLogger
import jetbrains.buildServer.terraformSupportPlugin.TerraformFeatureConfiguration
import jetbrains.buildServer.terraformSupportPlugin.TerraformRuntimeConstants

class WorkspaceNewCommand(
    myBuild: AgentRunningBuild,
    myLogger: FlowLogger,
    myConfiguration: TerraformFeatureConfiguration
) : BaseCommand(myBuild, myLogger, myConfiguration) {
    override fun prepareArguments(builder: CommandLineBuilder): CommandLineBuilder {
        builder.addArgument(argName = TerraformRuntimeConstants.PARAM_COMMAND_WORKSPACE)
        builder.addArgument(argName = TerraformRuntimeConstants.PARAM_COMMAND_NEW)
        builder.addArgument(myConfiguration.getWorkspaceName())

        return builder
    }

    override fun describe(): String {
        return "`terraform workspace new`"
    }
}