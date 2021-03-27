package jetbrains.buildServer.agent.terraformRunner.cmd

import jetbrains.buildServer.BuildProblemData
import jetbrains.buildServer.agent.BuildFinishedStatus
import jetbrains.buildServer.agent.BuildRunnerContext
import jetbrains.buildServer.agent.FlowGenerator
import jetbrains.buildServer.agent.runner.*
import jetbrains.buildServer.agent.terraformRunner.cmd.commands.TerraformCommandExecution

abstract class TerraformBuildService(
        protected val buildRunnerContext: BuildRunnerContext
) : MultiCommandBuildSession {
    protected val myFlowId: String = FlowGenerator.generateNewFlow()
    private val myCommands: List<TerraformCommandExecution> = this.instantiateCommands()
    private val myCommandIterator: Iterator<CommandExecution> = myCommands.iterator()

    abstract fun instantiateCommands(): List<TerraformCommandExecution>

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
        val problemCommands : List<TerraformCommandExecution> = myCommands.filter { it.hasProblem() }

        if (!myCommandIterator.hasNext() && problemCommands.isEmpty()) {
            return BuildFinishedStatus.FINISHED_SUCCESS
        }

        val logger = buildRunnerContext.build.buildLogger.getFlowLogger(myFlowId)

        problemCommands.forEach {
            val buildProblem = it.problemText
            logger.logBuildProblem(
                    BuildProblemData.createBuildProblem("Terraform command execution failed", "TerraformExecutionProblem", buildProblem)
            )
        }

        logger.disposeFlow()

        return BuildFinishedStatus.FINISHED_WITH_PROBLEMS
    }
}

