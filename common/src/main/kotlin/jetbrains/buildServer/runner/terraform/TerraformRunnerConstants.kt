package jetbrains.buildServer.runner.terraform

object TerraformRunnerConstants {
    // plugin-level data
    const val RUNNER_PLAN_DESCRIPTION = "Runner for 'terraform plan' execution"
    const val RUNNER_PLAN_DISPLAY_NAME = "Terraform: Plan"
    const val RUNNER_PLAN_TYPE = "terraform-plan-runner"

    const val RUNNER_APPLY_DESCRIPTION = "Runner for 'terraform apply' execution"
    const val RUNNER_APPLY_DISPLAY_NAME = "Terraform: Apply"
    const val RUNNER_APPLY_TYPE = "terraform-apply-runner"
}