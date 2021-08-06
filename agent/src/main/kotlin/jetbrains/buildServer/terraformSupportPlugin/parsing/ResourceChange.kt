package jetbrains.buildServer.terraformSupportPlugin.parsing

import com.google.gson.annotations.SerializedName

class ResourceChange(
    val name: String,
    val type: String,
    @SerializedName("provider_name")
    val providerName: String,
    @SerializedName("change")
    val changeItem: ResourceChangeItem
) {
    val statusEmoji: String
        get() {
            return when {
                changeItem.isCreated -> "&#x1F535;"
                changeItem.isChanged -> "&#x1F7E2;"
                changeItem.isReplaced -> "&#x1F7E0;"
                changeItem.isDeleted -> "&#x1F534;"
                else -> "&#128996;"
            }
        }
}