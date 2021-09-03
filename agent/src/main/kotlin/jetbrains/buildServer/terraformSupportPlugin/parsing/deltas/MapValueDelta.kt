package jetbrains.buildServer.terraformSupportPlugin.parsing.deltas

import com.google.common.collect.Maps

class MapValueDelta(
    name: String = "",
    override val before: Map<String, Any?> = mapOf(),
    override val after: Map<String, Any?> = mapOf()
) : ComplexValueDelta(name) {
    override val isList: Boolean
        get() = false

    override val isMap: Boolean
        get() = true

    override val getValues: List<ValueDelta>
        get() {
            val result = mutableListOf<ValueDelta>()
            when {
                before.isEmpty() -> {
                    after.forEach { (key, value) ->
                        result.add(
                            getValueDelta(key, null, value)
                        )
                    }
                }
                after.isEmpty() -> {
                    before.forEach { (key, value) ->
                        result.add(
                            getValueDelta(key, value, null)
                        )
                    }
                }
                else -> {
                    val diff = Maps.difference(before, after)
                    diff.entriesDiffering().forEach { (key, value) ->
                        result.add(
                            getValueDelta(key, value.leftValue(), value.rightValue())
                        )
                    }
                }
            }
            return result.filter { delta -> delta.isChanged }
        }
}