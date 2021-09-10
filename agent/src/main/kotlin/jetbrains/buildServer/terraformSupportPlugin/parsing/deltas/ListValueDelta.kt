package jetbrains.buildServer.terraformSupportPlugin.parsing.deltas

class ListValueDelta(
    name: String = "",
    override val before: List<Any> = listOf(),
    override val after: List<Any> = listOf()
) : ComplexValueDelta(name) {
    override val isList: Boolean
        get() = true

    override val isMap: Boolean
        get() = false

    override val getValues: List<ValueDelta>
        get() {
            val result = mutableListOf<ValueDelta>()
            before.forEach {
                if (!after.contains(it)) {
                    result.add(
                        getValueDelta(before = it, after = null)
                    )
                }
            }
            after.forEach {
                if (!before.contains(it)) {
                    result.add(
                        getValueDelta(before = null, after = it)
                    )
                }
            }
            return result
        }
}