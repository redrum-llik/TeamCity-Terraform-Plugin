package jetbrains.buildServer.runner.terraform

enum class TerraformCommandType(val id: String) {
    Init("init"),
    Plan("plan"),
    Apply("apply")
}