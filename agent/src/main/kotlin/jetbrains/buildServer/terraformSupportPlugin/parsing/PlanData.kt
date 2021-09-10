package jetbrains.buildServer.terraformSupportPlugin.parsing

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import jetbrains.buildServer.terraformSupportPlugin.parsing.deltas.SimpleValueDelta
import jetbrains.buildServer.terraformSupportPlugin.parsing.deltas.ValueDelta
import java.util.stream.Collectors

class PlanData(
    @JsonIgnore
    var fileName: String = "unknown",
    @JsonProperty("resource_changes")
    val resourceChanges: List<ResourceChange> = listOf(),
    @JsonProperty("output_changes")
    val outputChanges: Map<String, OutputChange> = mapOf()
) {
    val outputValuesDelta: List<ValueDelta>
        get() {
            return outputChanges
                .entries
                .stream()
                .map {
                    ValueDelta.getValueDelta(it.key, it.value.before, it.value.after)
                }
                .collect(Collectors.toList())
        }

    val changedOutputValuesDelta: List<ValueDelta>
        get() {
            return outputValuesDelta.filter { it.isChanged }
        }

    val hasChangedValues: Boolean
        get() {
            return changedOutputValuesDelta.isNotEmpty()
        }

    val changedResources: List<ResourceChange>
        get() {
            return resourceChanges.filter { it.changeItem.isChanged }
        }

    val hasChangedResources: Boolean
        get() {
            return changedResources.isNotEmpty()
        }
}