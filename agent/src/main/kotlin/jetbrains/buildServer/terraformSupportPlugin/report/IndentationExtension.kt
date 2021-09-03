package jetbrains.buildServer.terraformSupportPlugin.report

import com.mitchellbosecke.pebble.extension.AbstractExtension

class IndentationExtension : AbstractExtension() {
    override fun getFunctions(): MutableMap<String, com.mitchellbosecke.pebble.extension.Function> {
        return mutableMapOf(
            "indentation" to IndentationFunction()
        )
    }
}

