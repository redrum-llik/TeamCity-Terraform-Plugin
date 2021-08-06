package jetbrains.buildServer.terraformSupportPlugin.parsing

import com.google.gson.annotations.JsonAdapter

class ResourceChangeItem(
    actions: List<Action>,
    @JsonAdapter(ValueMapTypeAdapter::class)
    val before: Map<String, String>?,
    @JsonAdapter(ValueMapTypeAdapter::class)
    val after: Map<String, String>?
) : ActionDetails(actions) {
    val valueKeys: Set<String>
        get() {
            val keys = mutableSetOf<String>()
            if (before != null) {
                keys += before.keys
            }
            if (after != null) {
                keys += after.keys
            }
            return keys
        }

    val resourceValuesDelta: List<ValueDelta>
        get() {
            val valuesDelta = mutableListOf<ValueDelta>()
            valueKeys.forEach {
                valuesDelta.add(
                    ValueDelta(
                        it,
                        before?.get(it),
                        after?.get(it)
                    )
                )
            }
            return valuesDelta
        }

    val changedResourceValuesDelta: List<ValueDelta>
        get() {
            return resourceValuesDelta.filter { it.isChanged }
        }

    val hasChangedValues: Boolean
        get() {
            return changedResourceValuesDelta.isNotEmpty()
        }
}