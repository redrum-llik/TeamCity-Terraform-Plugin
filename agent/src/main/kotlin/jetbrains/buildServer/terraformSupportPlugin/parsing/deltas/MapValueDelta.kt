package jetbrains.buildServer.terraformSupportPlugin.parsing.deltas

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

            val keys = before.keys + after.keys

            for (key in keys) {
                result.add(
                    getValueDelta(
                        key,
                        before.getOrDefault(key, null),
                        after.getOrDefault(key, null)
                    )
                )
            }

            return result.filter { delta -> delta.isChanged }
        }
}