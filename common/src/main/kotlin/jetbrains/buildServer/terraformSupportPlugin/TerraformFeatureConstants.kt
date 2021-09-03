package jetbrains.buildServer.terraformSupportPlugin

object TerraformFeatureConstants {
    // plugin-level data
    const val FEATURE_DISPLAY_NAME = "Terraform Integration"
    const val FEATURE_TYPE = "terraform-integration"

    // feature parameters bean data
    const val FEATURE_SETTING_PLAN_JSON_FILE = "planJsonFile"
    const val FEATURE_SETTING_UPDATE_BUILD_STATUS = "updateBuildStatus"
    const val FEATURE_SETTING_PROTECTED_RESOURCES = "protectedResources"
    const val FEATURE_SETTING_SYSTEM_PROPERTIES = "systemProperties"

    // internal properties
    const val REPORT_TEMPLATE_FILE = "terraformChangesReportTemplate.pebble"
    const val REPORT_RESOURCE_FOLDER_PATH = "buildAgentResources"

    const val HIDDEN_ARTIFACT_REPORT_FILENAME = "terraformReport.html"
    const val HIDDEN_ARTIFACT_REPORT_FOLDER = ".teamcity/terraform"
    const val HIDDEN_ARTIFACT_PLAN_FILENAME = "out.json"
}

