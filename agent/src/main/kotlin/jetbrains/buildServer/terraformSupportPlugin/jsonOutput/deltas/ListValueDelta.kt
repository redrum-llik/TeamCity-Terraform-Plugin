package jetbrains.buildServer.terraformSupportPlugin.jsonOutput.deltas

import jetbrains.buildServer.terraformSupportPlugin.report.ComplexValueDeltaBracket

class ListValueDelta(
    name: String,
    deltas: List<ValueDelta>,
    forcesReplacement: Boolean
) : ComplexValueDelta(name, deltas, forcesReplacement) {
    override val isList: Boolean
        get() = true

    override val isMap: Boolean
        get() = false

    override val openingBracket: String
        get() = ComplexValueDeltaBracket.SQUARE_OPENING.symbol

    override val closingBracket: String
        get() = ComplexValueDeltaBracket.SQUARE_CLOSING.symbol
}