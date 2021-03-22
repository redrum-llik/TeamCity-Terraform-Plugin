package jetbrains.buildServer.runner.terraform

enum class TerraformVersionMode(val id: String) {
    Auto("Auto-detected"),
    TFEnv("tfenv")
}