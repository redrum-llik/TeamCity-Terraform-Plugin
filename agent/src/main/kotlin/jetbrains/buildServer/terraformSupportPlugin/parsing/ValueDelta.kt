package jetbrains.buildServer.terraformSupportPlugin.parsing

data class ValueDelta(
    val name: String,
    val before: String?,
    val after: String?
) {
    val isChanged: Boolean
        get() {
            return before != after
        }

    private fun isNull(value: String?): Boolean {
        return value.isNullOrEmpty() || value == "null"
    }

    val isAdded: Boolean
        get() {
            return isNull(before) && !isNull(after)
        }

    val isRemoved: Boolean
        get() {
            return !isNull(before) && isNull(after)
        }

    val isUpdated: Boolean
        get() {
            return isChanged && !isAdded && !isRemoved
        }
}