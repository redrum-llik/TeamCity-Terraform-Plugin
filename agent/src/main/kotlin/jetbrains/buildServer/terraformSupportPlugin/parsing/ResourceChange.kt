package jetbrains.buildServer.terraformSupportPlugin.parsing

import com.fasterxml.jackson.annotation.JsonProperty

class ResourceChange(
    val name: String,
    val type: String,
    val address: String,
    @JsonProperty("provider_name")
    val providerName: String,
    @JsonProperty("change")
    val changeItem: ResourceChangeItem
) {
    val statusColorCSSClass: String
        get() {
            return when {
                changeItem.isCreated -> "greenBackground"
                changeItem.isUpdated -> "blueBackground"
                changeItem.isReplaced -> "orangeBackground"
                changeItem.isDeleted -> "redBackground"
                else -> "fallbackBackground"
            }
        }
}