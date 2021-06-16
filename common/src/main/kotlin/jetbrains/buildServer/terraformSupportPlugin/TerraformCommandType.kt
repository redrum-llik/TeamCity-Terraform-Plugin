package jetbrains.buildServer.terraformSupportPlugin

enum class TerraformCommandType(val id: String) {
    CUSTOM("-Custom-"),
    INIT("init"),
    PLAN("plan"),
    APPLY("apply"),
    WORKSPACE("workspace");
}