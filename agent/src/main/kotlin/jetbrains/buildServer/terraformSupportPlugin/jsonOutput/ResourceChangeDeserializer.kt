package jetbrains.buildServer.terraformSupportPlugin.jsonOutput

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.module.kotlin.contains
import jetbrains.buildServer.terraformSupportPlugin.jsonOutput.model.Change
import jetbrains.buildServer.terraformSupportPlugin.jsonOutput.model.ResourceChange
import java.io.IOException

class ResourceChangeDeserializer(vc: Class<*>?) : StdDeserializer<ResourceChange>(vc) {
    constructor() : this(null)

    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserialize(
        jp: JsonParser,
        ctxt: DeserializationContext
    ): ResourceChange {
        val node: JsonNode = jp.codec.readTree(jp)

        val name: String = node["name"].asText()
        val index: String? = if (node.contains("index")) {
            node["index"].asText()
        } else {
            null
        }
        val type: String = node["type"].asText()
        val moduleAddress: String = node["module_address"].asText()

        val changeNode = node["change"]
        val parser = changeNode.traverse(jp.codec)
        parser.nextToken()

        val change: Change = ctxt.readValue(
            parser,
            Change::class.java
        )

        return ResourceChange(
            getNameWithIndex(name, index),
            type,
            moduleAddress,
            change
        )
    }

    private fun getNameWithIndex(name: String, index: String?): String {
        return if (index != null) {
            "$name.[$index]"
        } else {
            name
        }
    }
}