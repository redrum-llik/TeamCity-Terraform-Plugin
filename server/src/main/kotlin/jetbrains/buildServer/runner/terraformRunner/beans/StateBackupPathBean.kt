package jetbrains.buildServer.runner.terraformRunner.beans

import jetbrains.buildServer.runner.terraform.TerraformRunnerConstants

class StateBackupPathBean {
    val key = TerraformRunnerConstants.RUNNER_SETTING_APPLY_CUSTOM_BACKUP_KEY
    val label = "Custom backup path:"
    val description = "Custom path to the backup state file, absolute or relative to the working directory"
}