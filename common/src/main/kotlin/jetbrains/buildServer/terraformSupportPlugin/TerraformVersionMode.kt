package jetbrains.buildServer.terraformSupportPlugin

enum class TerraformVersionMode(val id: String) {
    AUTO("Auto-detect"),
    TFENV("Fetch with tfenv");
}