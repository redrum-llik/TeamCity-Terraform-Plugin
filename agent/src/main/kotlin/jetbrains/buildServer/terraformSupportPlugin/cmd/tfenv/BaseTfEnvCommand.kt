package jetbrains.buildServer.terraformSupportPlugin.cmd.tfenv

import jetbrains.buildServer.agent.AgentRunningBuild
import jetbrains.buildServer.agent.FlowLogger
import jetbrains.buildServer.agent.ToolCannotBeFoundException
import jetbrains.buildServer.terraformSupportPlugin.TerraformFeatureConfiguration
import jetbrains.buildServer.terraformSupportPlugin.TerraformFeatureConstants
import jetbrains.buildServer.terraformSupportPlugin.cmd.BaseCommand
import jetbrains.buildServer.terraformSupportPlugin.cmd.CommandLineBuilder
import java.io.File

abstract class BaseTfEnvCommand(
    myBuild: AgentRunningBuild,
    myLogger: FlowLogger,
    myConfiguration: TerraformFeatureConfiguration
) : BaseCommand(myBuild, myLogger, myConfiguration) {
    override fun getExecutablePath(): String {
        try {
            return File(myConfiguration.getTfEnvPathParameter()!!, TerraformFeatureConstants.TFENV_TOOL_EXECUTABLE_POSTFIX).absolutePath
        } catch (e: NullPointerException) {
            throw ToolCannotBeFoundException("tfenv version is not specified in the feature parameters")
        }
    }

    protected fun handleTerraformVersionParam(builder: CommandLineBuilder): CommandLineBuilder {
        val version = myConfiguration.getTerraformVersion()

        if (version.isNullOrBlank()) {
            myLogger.debug("No target version specified, tfenv will try to locate version reference in the state files")
        }
        else {
            builder.addArgument(value = version)
        }

        return builder
    }
}