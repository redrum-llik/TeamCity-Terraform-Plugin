package jetbrains.buildServer.runner.terraform

enum class TerraformCommandType(val id: String) {
    CUSTOM("-Custom-"),
    INIT("init"),
    PLAN("plan"),
    APPLY("apply");

    companion object {
        @JvmStatic
        fun fromValue(value: String): TerraformCommandType {
            return valueOf(value)
        }
    }
}