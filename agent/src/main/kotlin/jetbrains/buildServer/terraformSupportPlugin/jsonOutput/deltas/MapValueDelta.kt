package jetbrains.buildServer.terraformSupportPlugin.jsonOutput.deltas

import jetbrains.buildServer.terraformSupportPlugin.report.ComplexValueDeltaBracket

class MapValueDelta(
    name: String,
    deltas: List<ValueDelta>,
    forcesReplacement: Boolean
) : ComplexValueDelta(name, deltas, forcesReplacement) {
    override val isList: Boolean
        get() = false

    override val isMap: Boolean
        get() = true

    override val openingBracket: String
        get() = ComplexValueDeltaBracket.CURLY_OPENING.symbol

    override val closingBracket: String
        get() = ComplexValueDeltaBracket.CURLY_CLOSING.symbol
}