package jetbrains.buildServer.terraformSupportPlugin

import jetbrains.buildServer.serverSide.BuildFeature
import jetbrains.buildServer.web.openapi.PluginDescriptor
import jetbrains.buildServer.terraformSupportPlugin.TerraformFeatureConstants as CommonConst

class TerraformSupportBuildFeature(descriptor: PluginDescriptor) : BuildFeature() {
    private val myEditUrl = descriptor.getPluginResourcesPath(
        "editTerraformIntegrationSettings.jsp"
    )

    override fun getType(): String {
        return CommonConst.FEATURE_TYPE
    }

    override fun getDisplayName(): String {
        return CommonConst.FEATURE_DISPLAY_NAME
    }

    override fun getEditParametersUrl(): String? {
        return myEditUrl
    }

    override fun describeParameters(params: Map<String, String>): String {
        return buildString {
            val config = TerraformFeatureConfiguration(params)

            if (config.isReportEnabled()) {
                appendLine("Provide report tab on changes in '${config.getPlanJsonFile()}'")
            }

            if (config.updateBuildStatus()) {
                appendLine("Update build status with plan results")
            }

            if (config.hasProtectedResourcePattern()) {
                appendLine("Create build problem if any of the protected resources are marked for destroy: ${config.getProtectedResourcePattern()}")
            }
        }
    }
}