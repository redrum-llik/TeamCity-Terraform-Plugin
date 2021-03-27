package jetbrains.buildServer.agent.terraformRunner.detect

import jetbrains.buildServer.agent.BuildAgentConfiguration

interface TFEnvDetector {
    fun detectTFEnvInstances(buildAgentConfiguration: BuildAgentConfiguration): MutableMap<String, TFExecutableInstance>
}