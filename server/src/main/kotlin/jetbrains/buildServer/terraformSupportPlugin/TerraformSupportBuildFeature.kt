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

    override fun describeParameters(params: MutableMap<String, String>): String {
        return buildString {
            val config = TerraformFeatureConfiguration(params)

            if (config.useTfEnv()) {
                when {
                    config.getTerraformVersion().isNullOrEmpty() -> {
                        appendLine("Automatically detect suitable Terraform version with tfenv")
                    }
                    else -> {
                        appendLine("Install/use Terraform ${config.getTerraformVersion()} with tfenv")
                    }
                }
            }

            if (config.isReportEnabled()) {
                appendLine("Provide report tab on changes in '${config.getPlanFile()}'")
            }

            if (config.updateBuildStatus()) {
                appendLine("Update build status with plan results")
            }

            if (config.hasProtectedResources()) {
                appendLine("Create build problem if any of the protected resources are marked for destroy: ${config.getProtectedResources()}")
            }

            if (config.exportSystemProperties()) {
                appendLine("Export system properties to '${config.systemPropertiesOutFile()}' file")
            }
        }
    }
}