package jetbrains.buildServer.agent.terraformRunner.cmd

import com.intellij.openapi.diagnostic.Logger
import jetbrains.buildServer.agent.BuildFinishedStatus
import jetbrains.buildServer.agent.BuildRunnerContext
import jetbrains.buildServer.agent.FlowGenerator
import jetbrains.buildServer.agent.runner.*
import jetbrains.buildServer.runner.terraform.TerraformRunnerInstanceConfiguration

abstract class TerraformBuildService(
        private val buildRunnerContext: BuildRunnerContext,
        private val config: TerraformRunnerInstanceConfiguration
) : MultiCommandBuildSession {
    protected val myFlowId: String = FlowGenerator.generateNewFlow()
    protected abstract val myCommandIterator: Iterator<CommandExecution>

    abstract fun makeCommandLine(): ProgramCommandLine

    override fun getNextCommand(): CommandExecution? {
        return when {
            myCommandIterator.hasNext() -> {
                myCommandIterator.next()
            }
            else -> null
        }
    }

    override fun sessionStarted() {
    }

    override fun sessionFinished(): BuildFinishedStatus? {
        TODO("Not yet implemented")
    }
}

