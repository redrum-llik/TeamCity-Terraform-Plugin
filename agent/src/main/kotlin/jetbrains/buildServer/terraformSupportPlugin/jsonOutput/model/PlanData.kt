package jetbrains.buildServer.terraformSupportPlugin.jsonOutput.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import jetbrains.buildServer.log.Loggers

class PlanData(
    @JsonIgnore
    var fileName: String = "unknown",
    @JsonProperty("resource_changes")
    val resourceChanges: List<ResourceChange> = listOf(),
    @JsonProperty("output_changes")
    val outputChanges: Map<String, Change> = mapOf()
) {
    val changedOutputValues: Map<String, Change>
        get() {
            return outputChanges.filter { it.value.isChanged }
        }

    val hasChangedOutputValues: Boolean
        get() {
            for (value in changedOutputValues) {
                Loggers.AGENT.warn(value.value.getChangedValues.joinToString { it.name + "=" + it.changeType.symbol } + "|" + value.value.hasChangedValues)
            }
            return changedOutputValues.isNotEmpty()
        }

    val changedResources: List<ResourceChange>
        get() {
            return resourceChanges.filter { it.isChanged }
        }

    val hasChangedResources: Boolean
        get() {
            return changedResources.isNotEmpty()
        }

    val createdResources: List<ResourceChange>
        get() {
            return changedResources.filter { it.isCreated }
        }

    val updatedResources: List<ResourceChange>
        get() {
            return changedResources.filter { it.isUpdated }
        }

    val replacedResources: List<ResourceChange>
        get() {
            return changedResources.filter { it.isReplaced }
        }

    val deletedResources: List<ResourceChange>
        get() {
            return changedResources.filter { it.isDeleted }
        }
}