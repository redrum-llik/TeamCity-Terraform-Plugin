package jetbrains.buildServer.terraformSupportPlugin.parsing

import com.google.gson.annotations.SerializedName
import java.util.stream.Collectors

class PlanData(
    @Transient
    val fileName: String,
    @SerializedName("resource_changes")
    val resourceChanges: List<ResourceChange>,
    @SerializedName("output_changes")
    val outputChanges: Map<String, OutputChange>
) {
    val outputValuesDelta: List<ValueDelta>
        get() {
            return outputChanges
                .entries
                .stream()
                .map {
                    ValueDelta(
                        it.key, it.value.before.toString(), it.value.after.toString()
                    )
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