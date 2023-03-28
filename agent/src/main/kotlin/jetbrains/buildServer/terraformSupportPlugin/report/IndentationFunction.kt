package jetbrains.buildServer.terraformSupportPlugin.report

import io.pebbletemplates.pebble.template.EvaluationContext
import io.pebbletemplates.pebble.template.PebbleTemplate

class IndentationFunction : io.pebbletemplates.pebble.extension.Function {
    override fun getArgumentNames(): MutableList<String> = mutableListOf("level")

    override fun execute(
        args: Map<String?, Any?>,
        self: PebbleTemplate?,
        context: EvaluationContext?,
        lineNumber: Int
    ): Any {
        val level = args["level"] as Long?
        return "&emsp;".repeat((level ?: 0).toInt())
    }
}