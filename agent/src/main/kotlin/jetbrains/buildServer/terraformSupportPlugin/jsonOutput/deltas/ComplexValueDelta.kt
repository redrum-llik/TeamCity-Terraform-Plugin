package jetbrains.buildServer.terraformSupportPlugin.jsonOutput.deltas

abstract class ComplexValueDelta(
    name: String,
    override val deltas: List<ValueDelta>,
    forcesReplacement: Boolean
) : ValueDelta(name, forcesReplacement) {
    override val isComplex: Boolean
        get() = true
    abstract val isList: Boolean
    abstract val isMap: Boolean
    abstract val openingBracket: String
    abstract val closingBracket: String

    override val isAdded: Boolean
        get() {
            return getChangedValues.all { delta -> delta.isAdded }
        }

    override val isRemoved: Boolean
        get() {
            return getChangedValues.all { delta -> delta.isRemoved }
        }

    override val isUpdated: Boolean
        get() = isChanged && !(isAdded || isRemoved)

    override val isChanged: Boolean
        get() = getChangedValues.isNotEmpty()
}