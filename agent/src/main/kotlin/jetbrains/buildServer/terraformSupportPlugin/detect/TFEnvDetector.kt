package jetbrains.buildServer.terraformSupportPlugin.detect

import jetbrains.buildServer.agent.BuildAgentConfiguration

interface TFEnvDetector {
    fun detectTFEnvInstances(buildAgentConfiguration: BuildAgentConfiguration): MutableMap<String, TFExecutableInstance>
}