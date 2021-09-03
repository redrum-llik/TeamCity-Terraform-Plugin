package jetbrains.buildServer.terraformSupportPlugin.report

import com.mitchellbosecke.pebble.template.EvaluationContext
import com.mitchellbosecke.pebble.template.PebbleTemplate

class IndentationFunction : com.mitchellbosecke.pebble.extension.Function {
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