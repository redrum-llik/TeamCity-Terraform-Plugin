package jetbrains.buildServer.runner.terraform

class TerraformRunnerInstanceConfiguration(private val properties: Map<String, String>) {
    fun getCommand(): TerraformCommandType {
        return TerraformCommandType.valueOf(
                properties[TerraformRunnerConstants.RUNNER_SETTING_COMMAND_KEY]
                        ?: error("Specified value is not supported for this property")
        )
    }
}