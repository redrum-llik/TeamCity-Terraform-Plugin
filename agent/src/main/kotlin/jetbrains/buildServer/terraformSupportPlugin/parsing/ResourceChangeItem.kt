package jetbrains.buildServer.terraformSupportPlugin.parsing

import com.fasterxml.jackson.annotation.JsonProperty
import jetbrains.buildServer.terraformSupportPlugin.parsing.deltas.MapValueDelta

//@JsonIgnoreProperties
class ResourceChangeItem(
    @JsonProperty("actions")
    actions: List<Action> = listOf(Action.NO_OP),
    @JsonProperty("before")
    val before: Map<String, Any?> = mapOf(),
    @JsonProperty("after")
    val after: Map<String, Any?> = mapOf()
) : ActionDetails(actions) {
    val resourceValuesDelta: MapValueDelta
        get() {
            return MapValueDelta(before = before, after = after)
        }

    val hasChangedValues: Boolean
        get() {
            return resourceValuesDelta.getValues.isNotEmpty()
        }
}