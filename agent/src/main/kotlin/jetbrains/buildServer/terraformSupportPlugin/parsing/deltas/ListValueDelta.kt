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

            val size = maxOf(before.size, after.size)

            for (i in 0 until size) {
                result.add(
                    getValueDelta(
                        before = before.getOrNull(i),
                        after = after.getOrNull(i)
                    )
                )
            }

            return result
        }
}