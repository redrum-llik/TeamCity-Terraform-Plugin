package jetbrains.buildServer.terraformSupportPlugin.parsing.deltas

abstract class ComplexValueDelta(
    name: String = ""
) : ValueDelta(name) {
    override val isComplex: Boolean
        get() = true
    abstract val isList: Boolean
    abstract val isMap: Boolean
    abstract val getValues: List<ValueDelta>

    override val isChanged: Boolean
        get() {
            return when {
                !isComplex -> {
                    super.isChanged
                }
                else -> {
                    getValues.any { it -> it.isChanged }
                }
            }
        }
}