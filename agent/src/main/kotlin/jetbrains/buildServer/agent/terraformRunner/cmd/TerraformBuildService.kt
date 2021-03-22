package jetbrains.buildServer.agent.terraformRunner.cmd

import jetbrains.buildServer.agent.BuildFinishedStatus
import jetbrains.buildServer.agent.BuildRunnerContext
import jetbrains.buildServer.agent.FlowGenerator
import jetbrains.buildServer.agent.runner.*

abstract class TerraformBuildService(
        protected val buildRunnerContext: BuildRunnerContext
) : MultiCommandBuildSession {
    protected val myFlowId: String = FlowGenerator.generateNewFlow()
    private val myCommands: List<CommandExecution> = this.instantiateCommands()
    private val myCommandIterator: Iterator<CommandExecution> = myCommands.iterator()

    abstract fun instantiateCommands(): List<CommandExecution>

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

