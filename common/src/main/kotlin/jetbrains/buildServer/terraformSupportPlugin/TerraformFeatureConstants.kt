package jetbrains.buildServer.terraformSupportPlugin

import jetbrains.buildServer.tools.ToolVersionIdHelper

object TerraformFeatureConstants {
    // plugin-level data
    const val FEATURE_DISPLAY_NAME = "Terraform Integration"
    const val FEATURE_TYPE = "terraform-integration"

    // feature parameters bean data
    const val FEATURE_SETTING_USE_TFENV = "useTfEnv"
    const val FEATURE_SETTING_TFENV_TOOL_VERSION = "tfEnvVersion"
    const val FEATURE_SETTING_TERRAFORM_VERSION = "terraformVersion"
    const val FEATURE_SETTING_PLAN_FILE = "planFile"
    const val FEATURE_SETTING_TERRAFORM_WORKING_DIRECTORY = "terraformWorkingDirectory"
    const val FEATURE_SETTING_UPDATE_BUILD_STATUS = "updateBuildStatus"
    const val FEATURE_SETTING_PROTECTED_RESOURCES = "protectedResources"
    const val FEATURE_SETTING_SYSTEM_PROPERTIES = "systemProperties"

    // tfEnv tool data

    const val TFENV_TOOL_TYPE = "tfEnv"
    const val TFENV_TOOL_DISPLAY_NAME = "tfenv"
    const val TFENV_TOOL_DESCRIPTION = "Is used with Terraform build feature to install/switch Terraform versions on the agent machine."

    const val TFENV_TOOL_FETCH_URL = "teamcity.terraform.tfEnv.toolFetchUrl"

    const val REPORT_TEMPLATE_FILE = "terraformChangesReportTemplate.html"
    const val REPORT_RESOURCE_FOLDER_PATH = "/buildAgentResources"

    const val HIDDEN_ARTIFACT_REPORT_FILENAME = "terraformReport.html"
    const val HIDDEN_ARTIFACT_REPORT_FOLDER = ".teamcity/terraform"
    const val HIDDEN_ARTIFACT_PLAN_FILENAME = "tfplan"

    const val TFENV_TOOL_EXECUTABLE_POSTFIX = "bin"
}

