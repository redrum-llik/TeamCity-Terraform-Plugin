package jetbrains.buildServer.agent.terraformRunner

import com.intellij.openapi.diagnostic.Logger
import jetbrains.buildServer.agent.runner.BuildServiceAdapter
import jetbrains.buildServer.agent.runner.ProgramCommandLine
import jetbrains.buildServer.agent.runner.SimpleProgramCommandLine
import jetbrains.buildServer.runner.terraform.TerraformRunnerConstants as CommonConst
import jetbrains.buildServer.runner.terraform.TerraformRunnerInstanceConfiguration
import java.io.File
import java.nio.file.NoSuchFileException
import jetbrains.buildServer.agent.terraformRunner.TerraformCommandLineConstants as RunnerConst

class TerraformCommandBuildService : BuildServiceAdapter() {
    private val LOG = Logger.getInstance(this.javaClass.name)

    override fun makeProgramCommandLine(): ProgramCommandLine {
        TODO("Not yet implemented")
    }
}

