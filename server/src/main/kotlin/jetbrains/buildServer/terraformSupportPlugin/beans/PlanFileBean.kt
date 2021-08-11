package jetbrains.buildServer.terraformSupportPlugin.beans

import jetbrains.buildServer.terraformSupportPlugin.TerraformFeatureConstants

class PlanFileBean {
    val key = TerraformFeatureConstants.FEATURE_SETTING_PLAN_FILE
    val label = "Plan output file:"
    val description = "Relative path to the plan output file"
}