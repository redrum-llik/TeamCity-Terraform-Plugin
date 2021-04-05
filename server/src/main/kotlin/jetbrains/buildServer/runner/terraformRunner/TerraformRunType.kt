package jetbrains.buildServer.runner.terraformRunner

import jetbrains.buildServer.requirements.Requirement
import jetbrains.buildServer.requirements.RequirementType
import jetbrains.buildServer.runner.terraform.TerraformCommandType
import jetbrains.buildServer.runner.terraform.TerraformRunnerInstanceConfiguration
import jetbrains.buildServer.runner.terraform.TerraformVersionMode
import jetbrains.buildServer.serverSide.InvalidProperty
import jetbrains.buildServer.serverSide.PropertiesProcessor
import jetbrains.buildServer.serverSide.RunType
import jetbrains.buildServer.serverSide.RunTypeRegistry
import jetbrains.buildServer.web.openapi.PluginDescriptor
import java.util.*
import jetbrains.buildServer.runner.terraform.TerraformRunnerConstants as CommonConst


class TerraformRunType(runTypeRegistry: RunTypeRegistry, private val myDescriptor: PluginDescriptor) : RunType() {
    init {
        runTypeRegistry.registerRunType(this)
    }

    override fun getRunnerPropertiesProcessor(): PropertiesProcessor? {
        return ParametersValidator()
    }

    override fun getEditRunnerParamsJspFilePath(): String? {
        return myDescriptor.getPluginResourcesPath("terraformRunnerParams.jsp")
    }

    override fun getViewRunnerParamsJspFilePath(): String? {
        return myDescriptor.getPluginResourcesPath("viewTerraformRunnerParams.jsp")
    }

    override fun getDefaultRunnerProperties(): MutableMap<String, String> {
        return HashMap()
    }

    override fun getType(): String {
        return CommonConst.RUNNER_TYPE
    }

    override fun getDisplayName(): String {
        return CommonConst.RUNNER_DISPLAY_NAME
    }

    override fun getDescription(): String {
        return CommonConst.RUNNER_DESCRIPTION
    }

    override fun getRunnerSpecificRequirements(runParameters: MutableMap<String, String>): MutableList<Requirement> {
        val result: MutableList<Requirement> = ArrayList<Requirement>()
        val config = TerraformRunnerInstanceConfiguration(runParameters)
        when (config.getVersionMode()) {
            TerraformVersionMode.TFENV -> result.add(
                Requirement(
                        CommonConst.AGENT_PARAM_TFENV_VERSION,
                        null,
                        RequirementType.EXISTS
                )
            )
            TerraformVersionMode.AUTO -> result.add(
                Requirement(
                    CommonConst.AGENT_PARAM_TERRAFORM_VERSION,
                    null,
                    RequirementType.EXISTS
                )
            )
        }

        return result
    }

    companion object {
        class ParametersValidator : PropertiesProcessor {
            override fun process(properties: MutableMap<String, String>): MutableCollection<InvalidProperty> {
                val ret: MutableCollection<InvalidProperty> = ArrayList<InvalidProperty>(1)
                val config = TerraformRunnerInstanceConfiguration(properties)

                if (config.getCommand() == TerraformCommandType.CUSTOM && config.getCustomCommand().isNullOrEmpty()) {
                    ret.add(InvalidProperty(CommonConst.RUNNER_SETTING_CUSTOM_COMMAND_KEY, "Required parameter"))
                }

                return ret
            }
        }
    }
}