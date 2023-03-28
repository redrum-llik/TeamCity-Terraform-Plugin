package jetbrains.buildServer.terraformSupportPlugin.parsing.deltas

import com.fasterxml.jackson.core.JsonParseException
import jetbrains.buildServer.terraformSupportPlugin.TerraformSupport.Companion.getObjectMapper
import java.io.IOException

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
        fun getValueDelta(name: String = "", before: Any?, after: Any?): ValueDelta {
            val objectMapper = getObjectMapper()

            if (before is Map<*, *> || after is Map<*, *>) {
                return MapValueDelta(name, mapify(before), mapify(after))
            }

            if (before is List<*> || after is List<*>) {
                return ListValueDelta(name, listify(before), listify(after))
            }

            val beforeString = stringify(before)
            val afterString = stringify(after)

            try {
                val beforeNode = objectMapper.readTree(beforeString)

                if (beforeNode.isArray) {
                    val reader = objectMapper.readerForListOf(Any::class.java)
                    return ListValueDelta(
                        name,
                        reader.readValue(beforeString),
                        reader.readValue(afterString)
                    )
                } else if (beforeNode.isObject) {
                    val reader = objectMapper.readerForMapOf(Any::class.java)
                    return MapValueDelta(
                        name,
                        reader.readValue(beforeString),
                        reader.readValue(afterString)
                    )
                }
            } catch (_: IOException) {

            } catch (_: JsonParseException) {

            }

            return SimpleValueDelta(name, beforeString, afterString)
        }
    }
}