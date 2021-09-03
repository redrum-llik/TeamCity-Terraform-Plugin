package jetbrains.buildServer.terraformSupportPlugin.beans

import jetbrains.buildServer.terraformSupportPlugin.TerraformFeatureConstants

class PlanJsonFileBean {
    val key = TerraformFeatureConstants.FEATURE_SETTING_PLAN_JSON_FILE
    val label = "Plan changes file:"
    val description = "Relative path to the JSON file summarizing the planned changes"
}