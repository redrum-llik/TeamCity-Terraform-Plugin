package jetbrains.buildServer.runner.terraformRunner.discovery

import jetbrains.buildServer.runner.terraform.TerraformCommandType
import jetbrains.buildServer.runner.terraform.TerraformRunnerConstants
import jetbrains.buildServer.serverSide.discovery.BreadthFirstRunnerDiscoveryExtension
import jetbrains.buildServer.serverSide.discovery.DiscoveredObject
import jetbrains.buildServer.util.browser.Element

class TerraformRunnerDetector : BreadthFirstRunnerDiscoveryExtension() {
    private val TF_EXTENSION = ".tf"
    private val TF_JSON_EXTENSION = ".tf.json"

    override fun discoverRunnersInDirectory(
        dir: Element,
        filesAndDirs: MutableList<Element>
    ): MutableList<DiscoveredObject> {
        val defaultParams =
            listOf(TerraformRunnerConstants.RUNNER_SETTING_COMMAND_KEY to TerraformCommandType.PLAN.name)
        val commandDiscoveriesPairs = mutableListOf<List<Pair<String, String>>>()

        discoverTerraformStep(dir, filesAndDirs, commandDiscoveriesPairs)

        return commandDiscoveriesPairs
            .map { getTerraformDiscoveredObject(defaultParams + it) }
            .toMutableList()
    }

    private fun getTerraformDiscoveredObject(pairs: List<Pair<String, String>>) =
        DiscoveredObject(TerraformRunnerConstants.RUNNER_TYPE, pairs.toMap())

    private fun discoverTerraformStep(
        dir: Element,
        filesAndDirs: List<Element>,
        commandDiscoveriesPairs: MutableList<List<Pair<String, String>>>
    ) {
        if (isTerraformDiscovered(filesAndDirs)) {
            commandDiscoveriesPairs.add(
                listOf(
                    TerraformRunnerConstants.RUNNER_SETTING_ADDITIONAL_ARGS to "test"
                )
            )
        }
    }

    private fun isTerraformDiscovered(filesAndDirs: List<Element>): Boolean {
        return filesAndDirs.let {
            checkFileExtensionPresence(it, TF_EXTENSION) ||
                    checkFileExtensionPresence(it, TF_JSON_EXTENSION)
        }
    }

    private fun checkFileExtensionPresence(
        filesAndDirs: List<Element>,
        extension: String
    ): Boolean {
        return filesAndDirs.any { it.name.endsWith(extension) && it.isLeaf }
    }
}