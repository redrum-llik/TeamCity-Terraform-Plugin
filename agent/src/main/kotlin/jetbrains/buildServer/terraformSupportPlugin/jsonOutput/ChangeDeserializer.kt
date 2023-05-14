package jetbrains.buildServer.terraformSupportPlugin.jsonOutput

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.NullNode
import jetbrains.buildServer.terraformSupportPlugin.jsonOutput.deltas.ListValueDelta
import jetbrains.buildServer.terraformSupportPlugin.jsonOutput.deltas.MapValueDelta
import jetbrains.buildServer.terraformSupportPlugin.jsonOutput.deltas.SimpleValueDelta
import jetbrains.buildServer.terraformSupportPlugin.jsonOutput.deltas.ValueDelta
import jetbrains.buildServer.terraformSupportPlugin.jsonOutput.model.Action
import jetbrains.buildServer.terraformSupportPlugin.jsonOutput.model.Change
import java.io.IOException

class ChangeDeserializer(vc: Class<*>?) : StdDeserializer<Change>(vc) {
    constructor() : this(null)

    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserialize(
        jp: JsonParser,
        ctxt: DeserializationContext?
    ): Change {
        val node: JsonNode = jp.codec.readTree(jp)

        // upper level, just reading data

        val actions: List<Action> = node.get("actions").map {
            Action.fromString(
                it.asText()
            )
        }

        val before: JsonNode? = node.get("before")
        val after: JsonNode? = node.get("after")

        val replacePaths: List<List<String>> = try {
            node.get("replace_paths").map { replaceNode ->
                replaceNode.map { it.asText() }
            }
        } catch (e: NullPointerException) {
            listOf()
        }

        // parsing the deltas between "before" and "after" states of resource

        val delta = parseValueDelta(
            before = before,
            after = after,
            replacePaths = replacePaths
        )

        return Change(actions, delta)
    }

    private fun parseValueDelta(
        name: String = "",
        before: JsonNode?,
        after: JsonNode?,
        replacePaths: List<List<String>> = listOf()
    ): ValueDelta {
        return if (isObjectNode(before) || isObjectNode(after)) {
            parseMapValueDelta(name = name, before = before, after = after, replacePaths = replacePaths)
        } else if (isArrayNode(before) || isArrayNode(after)) {
            parseArrayValueDelta(name = name, before = before, after = after, replacePaths = replacePaths)
        } else {
            parsePrimitiveValueDelta(name = name, before = before, after = after, replacePaths = replacePaths)
        }
    }

    private fun isObjectNode(jsonNode: JsonNode?): Boolean {
        return if (jsonNode == null) {
            false
        } else if (jsonNode.isNull) {
            false
        } else jsonNode.isObject
    }

    private fun isArrayNode(jsonNode: JsonNode?): Boolean {
        return if (jsonNode == null) {
            false
        } else if (jsonNode.isNull) {
            false
        } else jsonNode.isArray
    }

    private fun parseMapValueDelta(
        name: String = "",
        before: JsonNode?,
        after: JsonNode?,
        replacePaths: List<List<String>> = listOf()
    ): MapValueDelta {
        val deltas: MutableList<ValueDelta> = mutableListOf()

        val keys = mutableSetOf<String>()

        var beforeMap: Map<String, JsonNode> = mapOf()
        var afterMap: Map<String, JsonNode> = mapOf()

        if (isObjectNode(before)) {
            beforeMap = before!! // asserted by isObjectNode
                .fields()
                .asSequence()
                .associateBy({ it.key }, { it.value })
            beforeMap
                .forEach { keys.add(it.key) }
        }

        if (isObjectNode(after)) {
            afterMap = after!! // asserted by isObjectNode
                .fields()
                .asSequence()
                .associateBy({ it.key }, { it.value })
            afterMap
                .forEach { keys.add(it.key) }
        }

        val matchingReplacePaths = getMatchingReplacePaths(name, replacePaths)

        for (key in keys) {
            deltas.add(
                parseValueDelta(
                    key,
                    beforeMap.getOrDefault(key, NullNode.getInstance()),
                    afterMap.getOrDefault(key, NullNode.getInstance()),
                    trimReplacePaths(matchingReplacePaths)
                )
            )
        }

        val forcesReplacement = matchingReplacePaths
            .any { it.size == 1 }

        return MapValueDelta(
            name = name,
            deltas = deltas,
            forcesReplacement = forcesReplacement
        )
    }

    private fun parseArrayValueDelta(
        name: String = "",
        before: JsonNode?,
        after: JsonNode?,
        replacePaths: List<List<String>> = listOf()
    ): ListValueDelta {
        val deltas: MutableList<ValueDelta> = mutableListOf()

        var beforeList: List<JsonNode> = listOf()
        var afterList: List<JsonNode> = listOf()

        if (isArrayNode(before)) {
            beforeList = before!! // asserted by isArrayNode
                .elements()
                .asSequence()
                .toMutableList()
        }

        if (isArrayNode(after)) {
            afterList = after!! // asserted by isArrayNode
                .elements()
                .asSequence()
                .toMutableList()
        }

        val size = maxOf(beforeList.size, afterList.size)

        for (i in 0 until size) {
            deltas.add(
                parseValueDelta(
                    before = beforeList.getOrNull(i),
                    after = afterList.getOrNull(i)
                )
            )
        }

        val forcesReplacement = getMatchingReplacePaths(name, replacePaths)
            .any { it.size == 1 }

        return ListValueDelta(
            name = name,
            deltas = deltas,
            forcesReplacement = forcesReplacement
        )
    }

    private fun parsePrimitiveValueDelta(
        name: String = "",
        before: JsonNode?,
        after: JsonNode?,
        replacePaths: List<List<String>> = listOf()
    ): ValueDelta {
        val beforeString = before?.asText()
        val afterString = after?.asText()

        val mapper = ParsingUtil.getObjectMapper()

        try {
            val beforeNode = if (beforeString == null) {
                NullNode.getInstance()
            } else {
                mapper.readTree(beforeString)
            }

            val afterNode = if (afterString == null) {
                NullNode.getInstance()
            } else {
                mapper.readTree(afterString)
            }

            if (beforeNode.isArray) {
                return parseArrayValueDelta(
                    name = name,
                    before = beforeNode,
                    after = afterNode
                )
            } else if (beforeNode.isObject) {
                return parseMapValueDelta(
                    name = name,
                    before = beforeNode,
                    after = afterNode
                )
            }
        } catch (_: IOException) {

        } catch (_: JsonParseException) {

        }

        val forcesReplacement = getMatchingReplacePaths(name, replacePaths)
            .any { it.size == 1 }

        return SimpleValueDelta(
            name = name,
            before = beforeString,
            after = afterString,
            forcesReplacement = forcesReplacement
        )
    }

    private fun getMatchingReplacePaths(
        name: String,
        replacePaths: List<List<String>>
    ): List<List<String>> {
        return replacePaths.filter {
            it.getOrNull(0) == name
        }
    }

    private fun trimReplacePaths(
        replacePaths: List<List<String>>
    ): List<List<String>> {
        val result = mutableListOf<List<String>>()

        replacePaths.forEach {
            if (it.size > 1) {
                result.add(it.slice(1..it.size))
            }
        }

        return result
    }
}