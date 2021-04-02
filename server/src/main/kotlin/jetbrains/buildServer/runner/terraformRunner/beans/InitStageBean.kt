package jetbrains.buildServer.runner.terraformRunner.beans

import jetbrains.buildServer.runner.terraform.TerraformRunnerConstants

class InitStageBean {
    val doInitKey = TerraformRunnerConstants.RUNNER_SETTING_INIT_STAGE_DO_INIT_KEY
    val doInitLabel = "Init:"
    val doInitDescription = "Run \"terraform init\" command before executing main command"

    val useWorkspaceKey = TerraformRunnerConstants.RUNNER_SETTING_INIT_STAGE_USE_WORKSPACE_KEY
    val useWorkspaceLabel = "Use workspace:"
    val useWorkspaceDescription = "Switch to specified workspace before executing main command"

    val createWorkspaceIfNotFoundKey = TerraformRunnerConstants.RUNNER_SETTING_INIT_STAGE_CREATE_WORKSPACE_IF_NOT_FOUND_KEY
    val createWorkspaceIfNotFoundLabel = "Create if not found:"
    val createWorkspaceIfNotFoundDescription = "Create specified workspace if it was not found"
}