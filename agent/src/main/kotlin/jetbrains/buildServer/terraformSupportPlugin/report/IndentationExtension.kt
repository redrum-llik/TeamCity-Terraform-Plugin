package jetbrains.buildServer.terraformSupportPlugin.report

import io.pebbletemplates.pebble.extension.AbstractExtension

class IndentationExtension : AbstractExtension() {
    override fun getFunctions(): MutableMap<String, io.pebbletemplates.pebble.extension.Function> {
        return mutableMapOf(
            "indentation" to IndentationFunction()
        )
    }
}

