package jetbrains.buildServer.agent.terraformRunner.cmd.init

import jetbrains.buildServer.agent.BuildRunnerContext
import jetbrains.buildServer.agent.runner.ProgramCommandLine
import jetbrains.buildServer.agent.terraformRunner.cmd.TerraformCommandExecution
import java.io.File

class InitCommandExecution(
        programCommandLine: ProgramCommandLine,
        buildRunnerContext: BuildRunnerContext,
        flowId: String
) : TerraformCommandExecution(programCommandLine, buildRunnerContext, flowId) {
    override fun processStarted(programCommandLine: String, workingDirectory: File) {
    }

    override fun makeProgramCommandLine(): ProgramCommandLine {
        TODO("Not yet implemented")
    }

    override fun beforeProcessStarted() {
    }
}