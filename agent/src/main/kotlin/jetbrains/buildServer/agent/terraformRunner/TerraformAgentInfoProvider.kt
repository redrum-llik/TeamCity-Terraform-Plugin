package jetbrains.buildServer.agent.terraformRunner

import com.intellij.openapi.diagnostic.Logger
import jetbrains.buildServer.agent.AgentLifeCycleAdapter
import jetbrains.buildServer.agent.AgentLifeCycleListener
import jetbrains.buildServer.agent.BuildAgent
import jetbrains.buildServer.agent.BuildAgentConfiguration
import jetbrains.buildServer.agent.terraformRunner.detect.TerraformDetector
import jetbrains.buildServer.agent.terraformRunner.detect.TerraformInstance
import jetbrains.buildServer.runner.terraform.getVersionedPathVarName
import jetbrains.buildServer.runner.terraform.TerraformRunnerConstants as CommonConst
import jetbrains.buildServer.util.EventDispatcher

class TerraformAgentInfoProvider(
    private val myConfig: BuildAgentConfiguration,
    events: EventDispatcher<AgentLifeCycleListener>,
    detectors: List<TerraformDetector>
) {
    private val myHolder = TerraformInstanceHolder()
    private val LOG = Logger.getInstance(this.javaClass.name)

    init {
        events.addListener(
            object : AgentLifeCycleAdapter() {
                override fun afterAgentConfigurationLoaded(agent: BuildAgent) {
                    registerDetectedTerraformInstances(detectors, agent.configuration)
                }
            }
        )
    }

    private fun registerDetectedTerraformInstances(
        detectors: List<TerraformDetector>,
        configuration: BuildAgentConfiguration?
    ) {
        for (detector in detectors) {
            LOG.debug("Detecting Terraform with ${detector.javaClass.name}.")
            for (entry in detector.detectTerraformInstances(configuration!!).entries) {
                LOG.debug("Processing detected Terraform instance [${entry.key}][${entry.value.version}]")
                myHolder.addInstance(entry.key, entry.value)
            }
        }

        if (!myHolder.isEmpty()) {
            registerMainInstance(myHolder.getMainInstance())
            registerInstances(myHolder.getInstances())
        } else {
            LOG.info(
                "No Terraform instance detected. If it is not available on PATH, " +
                        "please provide a custom path with ${CommonConst.BUILD_PARAM_SEARCH_PATH} agent property."
            )
        }
    }

    private fun registerInstances(instances: java.util.HashMap<String, TerraformInstance>) {
        for (instance in instances.values) {
            LOG.info("Registering detected Terraform instance at ${instance.executablePath}")

            val terraformVersionedPathName = getVersionedPathVarName(instance.version)
            myConfig.addConfigurationParameter(terraformVersionedPathName, instance.executablePath)
        }
    }

    private fun registerMainInstance(mainInstance: TerraformInstance) {
        LOG.info("Registering detected Terraform instance at ${mainInstance.executablePath} as main instance")

        myConfig.addConfigurationParameter(CommonConst.AGENT_PARAM_TERRAFORM_VERSION, mainInstance.version)
        myConfig.addConfigurationParameter(CommonConst.AGENT_PARAM_TERRAFORM_PATH, mainInstance.executablePath)
    }

    companion object {
        class TerraformInstanceHolder {
            private val myInstances = HashMap<String, TerraformInstance>()

            fun addInstance(path: String, terraformInstance: TerraformInstance) {
                myInstances[path] = terraformInstance
            }

            fun getInstances(): HashMap<String, TerraformInstance> {
                return myInstances
            }

            fun isEmpty(): Boolean {
                return myInstances.isEmpty()
            }

            fun getMainInstance(): TerraformInstance {
                return myInstances.values.maxOrNull()!!
            }
        }
    }
}