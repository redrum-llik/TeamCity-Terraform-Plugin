package jetbrains.buildServer.terraformSupportPlugin.jsonOutput.deltas

class SimpleValueDelta(
    name: String = "",
    val before: String?,
    val after: String?,
    forcesReplacement: Boolean
) : ValueDelta(name, forcesReplacement) {
    override val isComplex: Boolean
        get() = false

    override val deltas: List<ValueDelta>
        get() = listOf(this)

    private fun isNull(value: Any?): Boolean {
        return value == null || value == "null" || value == String()
    }

    override val isAdded: Boolean
        get() = (isNull(before) && !isNull(after)) && isChanged

    override val isRemoved: Boolean
        get() = (!isNull(before) && isNull(after)) && isChanged

    override val isChanged: Boolean
        get() = !areStringsEqual(before, after)

    override val isUpdated: Boolean
        get() = isChanged && !isAdded && !isRemoved

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

    private fun areStringsEqual(s1: String?, s2: String?): Boolean {
        val _s1 = if (s1.isNullOrEmpty() || s1 == "null") {
            ""
        } else {
            s1
        }

        val _s2 = if (s2.isNullOrEmpty() || s2 == "null") {
            ""
        } else {
            s2
        }

        return _s1 == _s2
    }
}