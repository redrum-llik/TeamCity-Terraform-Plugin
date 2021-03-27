package jetbrains.buildServer.agent.terraformRunner.detect

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.CapturingProcessHandler
import com.intellij.execution.process.ProcessNotCreatedException
import com.intellij.execution.process.ProcessOutput
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.text.StringUtil
import jetbrains.buildServer.agent.BuildAgentConfiguration
import jetbrains.buildServer.agent.terraformRunner.TerraformCommandLineConstants
import jetbrains.buildServer.runner.terraform.TerraformRunnerConstants
import java.nio.charset.StandardCharsets
import java.util.ArrayList

class TFEnvCommandLineDetector : TFEnvDetector {
    private val LOG = Logger.getInstance(this.javaClass.name)

    override fun detectTFEnvInstances(buildAgentConfiguration: BuildAgentConfiguration): MutableMap<String, TFExecutableInstance> {
        val instances = HashMap<String, TFExecutableInstance>()
        for (path in getSearchPaths(buildAgentConfiguration)) {
            val output = runDetectionCommand(path)
            if (output != null) {
                parseTFEnvInstances(instances, output, path)
            }
        }
        return instances
    }

    private fun getSearchPaths(buildAgentConfiguration: BuildAgentConfiguration): MutableList<SearchPath> {
        val searchPath = buildAgentConfiguration.configurationParameters[TerraformRunnerConstants.BUILD_PARAM_SEARCH_TF_PATH]
        val workDirPath = buildAgentConfiguration.workDirectory.toString()
        val result: MutableList<SearchPath> = ArrayList<SearchPath>()
        if (!searchPath.isNullOrBlank()) {
            result.add(SearchPath(searchPath))
        }
        result.add(SearchPath(workDirPath, true)) // if Terraform is available on PATH, any directory should be good to run detection
        return result
    }

    private fun runDetectionCommand(detectionPath: SearchPath): ProcessOutput? {
        val commandLine = GeneralCommandLine()
        commandLine.exePath = TerraformCommandLineConstants.COMMAND_TFENV
        commandLine.setWorkDirectory(detectionPath.path)

        LOG.debug("Detecting TFEnv in: $detectionPath")
        return handleDetectionProcess(commandLine)
    }

    private fun logDetectionProcessOutput(output: ProcessOutput) {
        val stdOut = output.stdout.trim { it <= ' ' }
        val stdErr = output.stderr.trim { it <= ' ' }
        val b = StringBuilder("TFEnv detection command output: \n")
        if (!StringUtil.isEmptyOrSpaces(stdOut)) {
            b.append("\n----- stdout: -----\n").append(stdOut).append("\n")
        }
        if (!StringUtil.isEmptyOrSpaces(stdErr)) {
            b.append("\n----- stderr: -----\n").append(stdErr).append("\n")
        }
        LOG.warn(b.toString())
    }

    private fun handleDetectionProcess(commandLine: GeneralCommandLine): ProcessOutput? {
        val output: ProcessOutput
        try {
            val handler = CapturingProcessHandler(commandLine.createProcess(), StandardCharsets.UTF_8)
            output = handler.runProcess()
            if (output == null) {
                return null
            }
        } catch (e: ProcessNotCreatedException) {
            return null
        }
        val errorOutput = output.stderr
        if (!errorOutput.isNullOrEmpty() && output.exitCode == 1) { //tfenv returns 1 if no command is supplied
            return output
        }
        logDetectionProcessOutput(output)
        return null
    }

    companion object {
        private val terraformVersionPattern = "tfenv ([0-9.]*)".toRegex()

        data class SearchPath(
                val path: String,
                val isDefault: Boolean = false
        )

        fun parseTFEnvInstances(
                instances: HashMap<String, TFExecutableInstance>,
                output: ProcessOutput,
                detectionPath: SearchPath
        ) {
            val outputLines = output.stderrLines //because of non-zero exit code output we are after is stored in stderr

            // process version line
            val result = terraformVersionPattern.find(outputLines[0])
                    ?: throw Exception("Could not parse TFEnv version.")
            val version = result.groupValues[1]

            instances[version] = TFExecutableInstance(
                    version,
                    detectionPath.path,
                    detectionPath.isDefault
            )
        }
    }
}