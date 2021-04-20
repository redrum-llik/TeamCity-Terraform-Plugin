package jetbrains.buildServer.agent.terraformRunner.cmd.commands

import com.google.gson.Gson
import jetbrains.buildServer.agent.BuildRunnerContext
import jetbrains.buildServer.agent.FlowLogger
import jetbrains.buildServer.agent.runner.CommandExecution
import jetbrains.buildServer.agent.runner.ProgramCommandLine
import jetbrains.buildServer.agent.runner.TerminationAction
import jetbrains.buildServer.agent.terraformRunner.TerraformCommandLineConstants as RunnerConst
import jetbrains.buildServer.agent.terraformRunner.cmd.CommandLineBuilder
import jetbrains.buildServer.messages.serviceMessages.ServiceMessage
import jetbrains.buildServer.messages.serviceMessages.ServiceMessageTypes
import jetbrains.buildServer.runner.terraform.TerraformRunnerInstanceConfiguration
import jetbrains.buildServer.util.StringUtil
import jetbrains.buildServer.runner.terraform.TerraformRunnerConstants as CommonConst

import java.io.File
import java.io.FileWriter
import java.util.*

abstract class BaseCommandExecution(
    val buildRunnerContext: BuildRunnerContext,
    flowId: String
) : CommandExecution {
    val buildProblemMaxLength = 25

    protected val myLogger: FlowLogger = buildRunnerContext.build.buildLogger.getFlowLogger(flowId)
    protected var myHasProblem: Boolean = false
    protected var myProblemIdentityHash: Int? = null

    abstract fun describe(): String

    private fun replacePasswords(text: String): String { //https://youtrack.jetbrains.com/issue/TW-45987
        val passwordReplacer = buildRunnerContext.build.passwordReplacer
        return passwordReplacer.replacePasswords(text)
    }

    override fun processStarted(programCommandLine: String, workingDirectory: File) {
        val serviceMessage = ServiceMessage.asString(
            ServiceMessageTypes.BLOCK_OPENED,
            mapOf("name" to replacePasswords(describe()))
        )
        myLogger.message(serviceMessage)
        myLogger.message("Starting: $programCommandLine")
    }

    override fun onStandardOutput(text: String) {
        text.lines().forEach {
            myLogger.message(it)

        }
    }

    override fun onErrorOutput(text: String) {
        text.lines().forEach {
            myLogger.error(it)
        }
        myProblemIdentityHash = text.hashCode()
    }

    override fun processFinished(exitCode: Int) {
        val serviceMessage = ServiceMessage.asString(
            ServiceMessageTypes.BLOCK_CLOSED,
            mapOf("name" to replacePasswords(describe()))
        )
        myLogger.message(serviceMessage)
        myLogger.apply {
            if (exitCode != 0) {
                myHasProblem = true
                error("Command failed with code $exitCode")
            }
        }
    }

    override fun interruptRequested(): TerminationAction = TerminationAction.KILL_PROCESS_TREE

    override fun isCommandLineLoggingEnabled(): Boolean = false

    protected open fun getExecutablePath(): String {
        if (
            buildRunnerContext.isVirtualContext ||
            !buildRunnerContext.configParameters.containsKey(
                CommonConst.AGENT_PARAM_TERRAFORM_PATH
            )
        ) {
            return RunnerConst.COMMAND_TERRAFORM
        }

        return File(
            buildRunnerContext.configParameters[CommonConst.AGENT_PARAM_TERRAFORM_PATH],
            RunnerConst.COMMAND_TERRAFORM
        ).absolutePath
    }

    protected open fun prepareArguments(
        config: TerraformRunnerInstanceConfiguration,
        builder: CommandLineBuilder
    ): CommandLineBuilder {
        return builder
    }

    protected open fun prepareCommonArguments(
        config: TerraformRunnerInstanceConfiguration,
        builder: CommandLineBuilder
    ): CommandLineBuilder {
        val additionalArgs = config.getAdditionalArgs()
        if (!additionalArgs.isNullOrEmpty()) {
            for (value in StringUtil.splitHonorQuotes(additionalArgs)) {
                builder.addArgument(value = value)
            }
        }

        if (config.getPassSystemParams()) {
            if (checkExtraVarsInAdditionalArgs(config)) {
                myLogger.warning("-var-file argument detected in additional arguments; TeamCity system parameters are skipped to avoid conflict")
            } else {
                builder.addArgument(
                    RunnerConst.PARAM_VAR_FILE,
                    saveArgumentsToFile()
                )
            }
        }

        return builder
    }

    private fun getArgumentRegex(argumentName: String): Regex {
        return "\\s?${argumentName}\\s".toRegex()
    }

    private fun checkExtraVarsInAdditionalArgs(
        config: TerraformRunnerInstanceConfiguration
    ): Boolean {
        if (!config.getAdditionalArgs().isNullOrEmpty()) {
            val additionalArgs = config.getAdditionalArgs()!!
            if (additionalArgs.contains(getArgumentRegex(RunnerConst.PARAM_VAR_FILE))
            ) {
                return true
            }
        }
        return false
    }

    private fun formatSystemProperties(): MutableMap<String, String> {
        val result: MutableMap<String, String> = HashMap()
        for (parameter in buildRunnerContext.build.sharedBuildParameters.systemProperties) {
            // Terraform variables cannot include dots
            val newKey = parameter.key.replace(".", "_")
            result[newKey] = parameter.value
        }
        return result
    }

    private fun saveArgumentsToFile(
    ): String {
        val varFile = File(
            buildRunnerContext.build.agentTempDirectory.absolutePath,
            "terraform_varfile_${UUID.randomUUID()}.json"
        ).normalize()
        val writer = FileWriter(varFile)
        val json = Gson().toJson(
            formatSystemProperties()
        )
        writer.run {
            write(json)
            close()
        }

        return varFile.absolutePath
    }

    override fun makeProgramCommandLine(): ProgramCommandLine {
        val builder = CommandLineBuilder()
        val config = TerraformRunnerInstanceConfiguration(buildRunnerContext.runnerParameters)

        builder.executablePath = getExecutablePath()
        builder.workingDir = buildRunnerContext.workingDirectory.path
        prepareArguments(config, builder)
        prepareCommonArguments(config, builder)

        return builder.build()
    }

    override fun beforeProcessStarted() {
    }

    fun hasProblem(): Boolean {
        return myHasProblem
    }

    fun problemIdentity(): String {
        return if (myProblemIdentityHash == null) describe() else myProblemIdentityHash!!.toString()
    }
}