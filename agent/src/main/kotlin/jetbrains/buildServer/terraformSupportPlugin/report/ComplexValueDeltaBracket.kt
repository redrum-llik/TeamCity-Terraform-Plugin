package jetbrains.buildServer.terraformSupportPlugin.report

enum class ComplexValueDeltaBracket(val symbol: String) {
    CURLY_OPENING("{"),
    CURLY_CLOSING("}"),
    SQUARE_OPENING("["),
    SQUARE_CLOSING("]")
}