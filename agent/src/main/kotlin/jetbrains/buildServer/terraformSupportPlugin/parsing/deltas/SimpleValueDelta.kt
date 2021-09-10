package jetbrains.buildServer.terraformSupportPlugin.parsing.deltas

class SimpleValueDelta(
    name: String = "",
    override val before: String?,
    override val after: String?
) : ValueDelta(name) {
    override val isComplex: Boolean
        get() = false

    val represent: String
        get() {
            return when {
                isAdded -> {
                    after.toString()
                }
                isUpdated -> {
                    "$before -> $after"
                }
                isRemoved -> {
                    "<s>$before</s>"
                }
                else -> { //should never reach here
                    "¯\\_(ツ)_/¯"
                }
            }
        }
}