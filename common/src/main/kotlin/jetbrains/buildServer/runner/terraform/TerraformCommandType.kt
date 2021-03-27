package jetbrains.buildServer.runner.terraform

enum class TerraformCommandType(val id: String) {
    INIT("init"),
    PLAN("plan"),
    APPLY("apply")
}