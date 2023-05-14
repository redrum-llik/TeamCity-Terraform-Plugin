package jetbrains.buildServer.terraformSupportPlugin.report

enum class ChangeType(val symbol: String, val cssClass: String) {
    // refer to the terraformChangesReport.css in server module
    ADDED("+", "greenTextColor"),
    REMOVED("-", "redTextColor"),
    UPDATED("~", "orangeTextColor"),
    FALLBACK("", "fallbackTextColor")
}