package jetbrains.buildServer.terraformSupportPlugin.report

enum class ChangeItemBackground(val cssClass: String) {
    // refer to the terraformChangesReport.css in server module
    GREEN("greenBackground"),
    BLUE("blueBackground"),
    ORANGE("orangeBackground"),
    RED("redBackground"),
    FALLBACK("fallbackBackground")
}