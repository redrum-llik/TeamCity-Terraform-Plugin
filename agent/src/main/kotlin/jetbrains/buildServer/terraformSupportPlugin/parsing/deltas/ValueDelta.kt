package jetbrains.buildServer.terraformSupportPlugin.parsing.deltas

abstract class ValueDelta(
    val name: String = ""
) {
    abstract val isComplex: Boolean
    abstract val before: Any?
    abstract val after: Any?

    open val isChanged: Boolean
        get() {
            return before != after
        }

    private fun isNull(value: Any?): Boolean {
        return value == null || value == "null" || value == String()
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

    val changeSymbol: String
        get() {
            return when {
                isAdded -> "+"
                isRemoved -> "-"
                isUpdated -> "~"
                else -> ""
            }
        }

    companion object {
        @JvmStatic
        private fun stringify(value: Any?): String {
            return when (value) {
                null -> {
                    ""
                }
                else -> {
                    value.toString()
                }
            }
        }

        @JvmStatic
        @Suppress("UNCHECKED_CAST")
        fun mapify(value: Any?): Map<String, Any> {
            return if (value != null) {
                value as Map<String, Any>
            } else {
                mapOf()
            }
        }

        @JvmStatic
        @Suppress("UNCHECKED_CAST")
        fun listify(value: Any?): List<Any> {
            return if (value != null) {
                value as List<Any>
            } else {
                listOf()
            }
        }

        @JvmStatic
        @Suppress("UNCHECKED_CAST")
        fun getValueDelta(name: String = "", before: Any?, after: Any?): ValueDelta {
            return when {
                before is Map<*, *> || after is Map<*, *> -> {
                    MapValueDelta(name, mapify(before), mapify(after))
                }
                before is List<*> || after is List<*> -> {
                    ListValueDelta(name, listify(before), listify(after))
                }
                else -> {
                    SimpleValueDelta(name, stringify(before), stringify(after))
                }
            }
        }
    }
}