package jetbrains.buildServer.terraformSupportPlugin.cmd.tfenv

import jetbrains.buildServer.agent.AgentRunningBuild
import jetbrains.buildServer.agent.BuildProgressLogger
import jetbrains.buildServer.agent.FlowLogger
import jetbrains.buildServer.agent.ToolCannotBeFoundException
import jetbrains.buildServer.terraformSupportPlugin.TerraformFeatureConfiguration
import jetbrains.buildServer.terraformSupportPlugin.TerraformFeatureConstants
import jetbrains.buildServer.terraformSupportPlugin.TerraformRuntimeConstants
import jetbrains.buildServer.terraformSupportPlugin.cmd.BaseCommand
import jetbrains.buildServer.terraformSupportPlugin.cmd.CommandLineBuilder
import java.io.File

abstract class BaseTfEnvCommand(
    myBuild: AgentRunningBuild,
    myLogger: BuildProgressLogger,
    myConfiguration: TerraformFeatureConfiguration
) : BaseCommand(myBuild, myLogger, myConfiguration) {
    override fun getWorkingDir(): String {
        try {
            return File(
                myConfiguration.getTfEnvPathParameter()!!,
                TerraformFeatureConstants.TFENV_TOOL_EXECUTABLE_POSTFIX
            ).absolutePath
        } catch (e: NullPointerException) {
            throw ToolCannotBeFoundException("tfenv version is not specified in the feature parameters")
        }
    }

    override fun getExecutablePath(): String {
        return TerraformRuntimeConstants.COMMAND_TFENV
    }

    protected fun handleTerraformVersionParam(builder: CommandLineBuilder): CommandLineBuilder {
        val version = myConfiguration.getTerraformVersion()

        if (!version.isNullOrBlank()) {
            builder.addArgument(value = version)

        }

        return builder
    }
}