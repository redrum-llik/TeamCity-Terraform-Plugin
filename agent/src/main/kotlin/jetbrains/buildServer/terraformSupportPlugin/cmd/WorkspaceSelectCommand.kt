package jetbrains.buildServer.terraformSupportPlugin.cmd

import com.intellij.execution.process.ProcessOutput
import jetbrains.buildServer.agent.AgentRunningBuild
import jetbrains.buildServer.agent.FlowLogger
import jetbrains.buildServer.terraformSupportPlugin.TerraformFeatureConfiguration
import jetbrains.buildServer.terraformSupportPlugin.TerraformRuntimeConstants

class WorkspaceSelectCommand(
    myBuild: AgentRunningBuild,
    myLogger: FlowLogger,
    myConfiguration: TerraformFeatureConfiguration
) : BaseCommand(myBuild, myLogger, myConfiguration) {
    override fun prepareArguments(builder: CommandLineBuilder): CommandLineBuilder {
        builder.addArgument(argName = TerraformRuntimeConstants.PARAM_COMMAND_WORKSPACE)
        builder.addArgument(argName = TerraformRuntimeConstants.PARAM_COMMAND_SELECT)
        builder.addArgument(myConfiguration.getWorkspaceName())

        return builder
    }

    companion object {
        private val noWorkspaceFoundPattern = "Workspace \".*\" doesn't exist\\.".toRegex()

        private fun findInErrorOutput(output: ProcessOutput, pattern: Regex): MatchResult? {
            output.stderrLines.forEach {
                val result = pattern.find(it)
                if (result != null) return result
            }
            return null
        }

        fun checkIfNoWorkspaceFoundErrorInOutput(output: ProcessOutput): Boolean {
            return findInErrorOutput(output, noWorkspaceFoundPattern) != null
        }
    }

    override fun describe(): String {
        return "`terraform workspace select`"
    }
}