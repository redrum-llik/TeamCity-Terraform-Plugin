package jetbrains.buildServer.terraformSupportPlugin

import jetbrains.buildServer.tools.ToolVersionIdHelper

object TerraformFeatureConstants {
    // plugin-level data
    const val FEATURE_DISPLAY_NAME = "Terraform integration"
    const val FEATURE_TYPE = "terraform-integration"

    // feature parameters bean data
    const val FEATURE_SETTING_USE_TFENV = "useTfEnv"
    const val FEATURE_SETTING_TFENV_TOOL_VERSION = "tfEnvVersion"
    const val FEATURE_SETTING_TERRAFORM_VERSION = "terraformVersion"
    const val FEATURE_SETTING_SYSTEM_PROPERTIES = "systemProperties"

    // tfEnv tool data

    const val TFENV_TOOL_TYPE = "tfEnv"
    const val TFENV_TOOL_DISPLAY_NAME = "tfenv"
    const val TFENV_TOOL_DESCRIPTION = "Is used with Terraform build feature to install/switch Terraform versions on the agent machine."

    const val BUILD_PARAM_REPORT_ENABLED = "teamcity.terraform.report.enabled"
    const val TFENV_TOOL_FETCH_URL = "teamcity.terraform.tfEnv.toolFetchUrl"

    const val HIDDEN_ARTIFACT_REPORT_FILENAME = "terraformReport.html"
    const val HIDDEN_ARTIFACT_REPORT_FOLDER = ".teamcity/terraform"

    const val TFENV_TOOL_EXECUTABLE_POSTFIX = "bin"
}

