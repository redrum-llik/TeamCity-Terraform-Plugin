package jetbrains.buildServer.runner.terraform

enum class TerraformCommandType(val id: String) {
    CUSTOM("-Custom-"),
    INIT("init"),
    PLAN("plan"),
    APPLY("apply"),
    WORKSPACE("workspace");
}