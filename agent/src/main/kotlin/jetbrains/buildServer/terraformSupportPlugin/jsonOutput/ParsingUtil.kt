package jetbrains.buildServer.terraformSupportPlugin.jsonOutput

import com.fasterxml.jackson.core.json.JsonReadFeature
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import jetbrains.buildServer.terraformSupportPlugin.jsonOutput.model.Change
import jetbrains.buildServer.terraformSupportPlugin.jsonOutput.model.ResourceChange

class ParsingUtil {
    companion object {
        fun getObjectMapper(): ObjectMapper {
            val kotlinModule = KotlinModule.Builder()
                .configure(KotlinFeature.NullIsSameAsDefault, enabled = true)
                .build()

            val deltasModule = SimpleModule()
            deltasModule.addDeserializer(
                Change::class.java,
                ChangeDeserializer()
            )
            deltasModule.addDeserializer(
                ResourceChange::class.java,
                ResourceChangeDeserializer()
            )

            return JsonMapper.builder()
                .enable(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModule(kotlinModule)
                .addModule(deltasModule)
                .build()
        }
    }
}