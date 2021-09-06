package jetbrains.buildServer.terraformSupportPlugin.detect

import jetbrains.buildServer.agent.BuildAgentConfiguration

interface TerraformDetector {
    fun detectTerraformInstances(buildAgentConfiguration: BuildAgentConfiguration): MutableMap<String, TFExecutableInstance>
}