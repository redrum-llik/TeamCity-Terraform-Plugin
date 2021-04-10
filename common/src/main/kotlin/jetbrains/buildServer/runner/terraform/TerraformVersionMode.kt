package jetbrains.buildServer.runner.terraform

enum class TerraformVersionMode(val id: String) {
    AUTO("Auto-detect"),
    TFENV("Fetch with tfenv");
}