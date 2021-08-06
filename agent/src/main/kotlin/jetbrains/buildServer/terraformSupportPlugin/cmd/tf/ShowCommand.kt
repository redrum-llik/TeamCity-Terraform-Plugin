package jetbrains.buildServer.terraformSupportPlugin.cmd.tf

import jetbrains.buildServer.agent.AgentRunningBuild
import jetbrains.buildServer.agent.BuildProgressLogger
import jetbrains.buildServer.terraformSupportPlugin.TerraformFeatureConfiguration
import jetbrains.buildServer.terraformSupportPlugin.TerraformRuntimeConstants
import jetbrains.buildServer.terraformSupportPlugin.cmd.BaseCommand
import jetbrains.buildServer.terraformSupportPlugin.cmd.CommandLineBuilder
import java.io.File

class ShowCommand(
    myBuild: AgentRunningBuild,
    myLogger: BuildProgressLogger,
    myConfiguration: TerraformFeatureConfiguration,
    private val myInputFile: File?
) : BaseCommand(myBuild, myLogger, myConfiguration) {
    override fun prepareArguments(builder: CommandLineBuilder): CommandLineBuilder {
        builder.addArgument(TerraformRuntimeConstants.PARAM_COMMAND_SHOW)
        builder.addArgument(TerraformRuntimeConstants.PARAM_COMMAND_JSON)
        if (myInputFile != null && myInputFile.exists()) {
            builder.addArgument(myInputFile.absolutePath)
        }

        return builder
    }

    override fun getWorkingDir(): String {
        return when {
            myInputFile != null -> {
                myInputFile.parent
            }
            else -> {
                super.getWorkingDir()
            }
        }
    }

    override fun describe(): String {
        return "`terraform show`"
    }
}