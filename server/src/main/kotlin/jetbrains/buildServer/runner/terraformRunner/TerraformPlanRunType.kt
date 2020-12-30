package jetbrains.buildServer.runner.terraformRunner

import jetbrains.buildServer.requirements.Requirement
import jetbrains.buildServer.requirements.RequirementType
import jetbrains.buildServer.runner.terraform.TerraformRunnerConstants as CommonConst
import jetbrains.buildServer.runner.terraform.TerraformRunnerInstanceConfiguration
import jetbrains.buildServer.serverSide.InvalidProperty
import jetbrains.buildServer.serverSide.PropertiesProcessor
import jetbrains.buildServer.serverSide.RunType
import jetbrains.buildServer.serverSide.RunTypeRegistry
import jetbrains.buildServer.web.openapi.PluginDescriptor
import java.util.*


class TerraformPlanRunType(runTypeRegistry: RunTypeRegistry, private val myDescriptor: PluginDescriptor) : RunType() {
    init {
        runTypeRegistry.registerRunType(this)
    }

    override fun getRunnerPropertiesProcessor(): PropertiesProcessor? {
        return ParametersValidator()
    }

    override fun getEditRunnerParamsJspFilePath(): String? {
        return myDescriptor.getPluginResourcesPath("terraformPlanRunnerParams.jsp")
    }

    override fun getViewRunnerParamsJspFilePath(): String? {
        return myDescriptor.getPluginResourcesPath("viewTerraformPlanRunnerParams.jsp")
    }

    override fun getDefaultRunnerProperties(): MutableMap<String, String> {
        val map: MutableMap<String, String> = HashMap()
        return map;
    }

    override fun getType(): String {
        return CommonConst.RUNNER_PLAN_TYPE
    }

    override fun getDisplayName(): String {
        return CommonConst.RUNNER_PLAN_DISPLAY_NAME
    }

    override fun getDescription(): String {
        return CommonConst.RUNNER_PLAN_DESCRIPTION
    }

    override fun getRunnerSpecificRequirements(runParameters: MutableMap<String, String>): MutableList<Requirement> {
        val result: MutableList<Requirement> = ArrayList<Requirement>()
        //result.add(
        //   Requirement(
        //        CommonConst.AGENT_PARAM_ANSIBLE_PATH,
        //        null,
        //        RequirementType.EXISTS
        //    )
        //)
        return result
    }

    companion object {
        class ParametersValidator : PropertiesProcessor {
            override fun process(properties: MutableMap<String, String>): MutableCollection<InvalidProperty> {
                val ret: MutableCollection<InvalidProperty> = ArrayList<InvalidProperty>(1)
                val config = TerraformRunnerInstanceConfiguration(properties)
                //if (config.getPlaybook().isNullOrEmpty()) {
                //    ret.add(InvalidProperty(CommonConst.RUNNER_SETTING_PLAYBOOK_FILE, "Required parameter"))
                //}
                return ret
            }
        }
    }
}