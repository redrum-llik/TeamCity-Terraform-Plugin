package jetbrains.buildServer.terraformSupportPlugin.beans

import jetbrains.buildServer.terraformSupportPlugin.TerraformRunnerConstants
import jetbrains.buildServer.terraformSupportPlugin.TerraformVersionMode

class VersionBean {
    val key = TerraformRunnerConstants.RUNNER_SETTING_VERSION_KEY
    val label = "Version:"

    val autoDetectModeKey = TerraformVersionMode.AUTO.name
    val autoDetectModeValue = TerraformVersionMode.AUTO.id
    val tfEnvModeKey = TerraformVersionMode.TFENV.name
    val tfEnvModeValue = TerraformVersionMode.TFENV.id

    val tfEnvKey = TerraformRunnerConstants.RUNNER_SETTING_VERSION_TFENV_VERSION
    val tfEnvLabel = "Version to use:"
    val tfEnvDescription = "Terraform version to switch to via tfenv"
}