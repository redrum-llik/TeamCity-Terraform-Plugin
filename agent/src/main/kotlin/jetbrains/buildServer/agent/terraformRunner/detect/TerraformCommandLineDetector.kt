package jetbrains.buildServer.agent.terraformRunner.detect

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.CapturingProcessHandler
import com.intellij.execution.process.ProcessNotCreatedException
import com.intellij.execution.process.ProcessOutput
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.text.StringUtil
import jetbrains.buildServer.agent.BuildAgentConfiguration
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.collections.HashMap
import jetbrains.buildServer.runner.terraform.TerraformCommandLineConstants as RunnerConst
import jetbrains.buildServer.runner.terraform.TerraformRunnerConstants as CommonConst


class TerraformCommandLineDetector : TerraformDetector {
    private val logger = Logger.getInstance(this.javaClass.name)

    override fun detectTerraformInstances(buildAgentConfiguration: BuildAgentConfiguration): MutableMap<String, TFExecutableInstance> {
        val instances = HashMap<String, TFExecutableInstance>()
        for (path in getSearchPaths(buildAgentConfiguration)) {
            val output = runDetectionCommand(path)
            if (output != null) {
                parseTerraformInstance(instances, output, path)
            }
        }
        return instances
    }

    private fun getSearchPaths(buildAgentConfiguration: BuildAgentConfiguration): MutableList<SearchPath> {
        val searchPath = buildAgentConfiguration.configurationParameters[CommonConst.BUILD_PARAM_SEARCH_TF_PATH]
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
        commandLine.exePath = RunnerConst.COMMAND_TERRAFORM
        commandLine.addParameter(RunnerConst.PARAM_VERSION)
        commandLine.setWorkDirectory(detectionPath.path)

        logger.debug("Detecting Terraform in: $detectionPath")
        return handleDetectionProcess(commandLine)
    }

    private fun logDetectionProcessOutput(output: ProcessOutput) {
        val stdOut = output.stdout.trim { it <= ' ' }
        val stdErr = output.stderr.trim { it <= ' ' }
        val b = StringBuilder("Terraform detection command output: \n")
        if (!StringUtil.isEmptyOrSpaces(stdOut)) {
            b.append("\n----- stdout: -----\n").append(stdOut).append("\n")
        }
        if (!StringUtil.isEmptyOrSpaces(stdErr)) {
            b.append("\n----- stderr: -----\n").append(stdErr).append("\n")
        }
        logger.debug(b.toString())
    }

    private fun handleDetectionProcess(commandLine: GeneralCommandLine): ProcessOutput? {
        val output: ProcessOutput
        try {
            val handler = CapturingProcessHandler(commandLine.createProcess(), StandardCharsets.UTF_8)
            output = handler.runProcess()
            if (output == null) {
                return null
            }
        }
        catch (e: ProcessNotCreatedException) {
            return null
        }
        val errorOutput = output.stderr
        if (!errorOutput.isNullOrEmpty()) {
            logDetectionProcessOutput(output)
            return null
        }
        return output
    }

    companion object {
        private val terraformVersionPattern = "v([0-9.]*)".toRegex()
        private val LOG = Logger.getInstance(this::class.java.name)

        data class SearchPath(
            val path: String,
            val isDefault: Boolean = false
        )

        fun parseTerraformInstance(
                instances: HashMap<String, TFExecutableInstance>,
                output: ProcessOutput,
                detectionPath: SearchPath
        ) {
            val outputLines = output.stdoutLines

            // process version line
            val result = terraformVersionPattern.find(outputLines[0])
                ?: throw Exception("Could not parse Terraform version.")
            val version = result.groupValues[0]

            instances[version] = TFExecutableInstance(
                version,
                detectionPath.path,
                detectionPath.isDefault
            )

            when (detectionPath.isDefault) {
                true -> LOG.info("Found Terraform $version instance in PATH")
                false -> LOG.info("Found Terraform $version instance in ${detectionPath.path}")
            }
        }
    }
}