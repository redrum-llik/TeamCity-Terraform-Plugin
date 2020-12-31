package jetbrains.buildServer.agent.terraformRunner.detect

import jetbrains.buildServer.agent.BuildAgentConfiguration

interface TerraformDetector {
    fun detectTerraformInstances(buildAgentConfiguration: BuildAgentConfiguration): MutableMap<String, TerraformInstance>
}