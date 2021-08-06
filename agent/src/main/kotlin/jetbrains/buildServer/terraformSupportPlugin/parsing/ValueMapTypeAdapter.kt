package jetbrains.buildServer.terraformSupportPlugin.parsing

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

class ValueMapTypeAdapter : TypeAdapter<Map<String, String>>() {
    override fun write(out: JsonWriter?, value: Map<String, String>?) {
        TODO("Not yet implemented")
    }

    private fun readObject(`in`: JsonReader): Map<String, String> {
        val result = mutableMapOf<String, String>()

        `in`.beginObject()
        while (`in`.hasNext()) {
            val fieldName = `in`.nextName()
            when {
                `in`.peek() == JsonToken.BEGIN_OBJECT -> {
                    result[fieldName] = readObject(`in`).toString()
                }
                `in`.peek() == JsonToken.BEGIN_ARRAY -> {
                    result[fieldName] = readArray(`in`).toString()
                }
                `in`.peek() == JsonToken.BOOLEAN -> {
                    result[fieldName] = `in`.nextBoolean().toString()
                }
                `in`.peek() == JsonToken.NULL -> {
                    `in`.skipValue()
                }
                `in`.peek() == JsonToken.END_OBJECT -> {
                    `in`.endObject()
                    return result
                }
                else -> {
                    result[fieldName] = `in`.nextString()
                }
            }
        }
        `in`.endObject()
        return result
    }

    private fun readArray(`in`: JsonReader): List<Any> {
        val result = mutableListOf<String>()

        `in`.beginArray()
        while (`in`.hasNext()) {
            when {
                `in`.peek() == JsonToken.BEGIN_ARRAY -> {
                    result.add(
                        readArray(`in`).toString()
                    )
                }
                `in`.peek() == JsonToken.BEGIN_OBJECT -> {
                    result.add(
                        readObject(`in`).toString()
                    )
                }
                `in`.peek() == JsonToken.BOOLEAN -> {
                    result.add(
                        `in`.nextBoolean().toString()
                    )
                }
                `in`.peek() == JsonToken.NULL -> {
                    `in`.skipValue()
                }
                `in`.peek() == JsonToken.END_ARRAY -> {
                    `in`.endArray()
                    return result
                }
                else -> {
                    result.add(
                        `in`.nextString()
                    )
                }
            }
        }
        `in`.endArray()
        return result
    }

    override fun read(`in`: JsonReader?): Map<String, String> {
        return readObject(`in`!!)
    }
}
