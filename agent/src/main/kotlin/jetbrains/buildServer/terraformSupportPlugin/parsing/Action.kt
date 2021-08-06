package jetbrains.buildServer.terraformSupportPlugin.parsing

import com.google.gson.annotations.SerializedName

enum class Action {
    @SerializedName("no-op")
    NO_OP,
    @SerializedName("create")
    CREATE,
    @SerializedName("read")
    READ,
    @SerializedName("update")
    UPDATE,
    @SerializedName("delete")
    DELETE;
}