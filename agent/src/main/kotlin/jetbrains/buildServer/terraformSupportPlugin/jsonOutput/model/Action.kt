package jetbrains.buildServer.terraformSupportPlugin.jsonOutput.model

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

    companion object {
        fun fromString(string: String): Action {
            when (string) {
                "create" -> {
                    return CREATE
                }
                "read" -> {
                    return READ
                }
                "update" -> {
                    return UPDATE
                }
                "delete" -> {
                    return DELETE
                }
                else -> {
                    return NO_OP
                }
            }
        }
    }
}