package jetbrains.buildServer.terraformSupportPlugin.parsing

import com.fasterxml.jackson.annotation.JsonProperty

enum class Action {
    @JsonProperty("no-op")
    NO_OP,

    @JsonProperty("create")
    CREATE,

    @JsonProperty("read")
    READ,

    @JsonProperty("update")
    UPDATE,

    @JsonProperty("delete")
    DELETE;
}