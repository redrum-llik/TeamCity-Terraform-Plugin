package jetbrains.buildServer.terraformSupportPlugin.tfEnvTool

import com.google.gson.Gson
import com.google.gson.JsonArray
import jetbrains.buildServer.log.Loggers
import jetbrains.buildServer.serverSide.TeamCityProperties
import jetbrains.buildServer.terraformSupportPlugin.TerraformFeatureConstants
import jetbrains.buildServer.tools.available.FetchAvailableToolsResult
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern

class TfEnvAvailableToolsFetcherImpl: TfEnvAvailableToolsFetcher {
    private val TOOL_REPOSITORY_URL = "https://api.github.com/repos/tfutils/tfenv/releases"
    private val VERSION_PATTERN = Pattern.compile("v[0-9]+\\.[0-9]+\\.[0-9]+")

    override fun fetchAvailable(): FetchAvailableToolsResult {
        val url = URL(TeamCityProperties.getProperty(TerraformFeatureConstants.TFENV_TOOL_FETCH_URL, TOOL_REPOSITORY_URL))
        try {
            val conn = url.openConnection()
            val json = BufferedReader(InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)).readText()
            val gson = Gson()
            val releases = gson.fromJson(json, JsonArray::class.java)
            val tools = releases
                .asSequence()
                .filter { it.isJsonObject }
                .map { it.asJsonObject["tag_name"] }
                .filter { it != null }
                .map { it.asString }
                .filter { VERSION_PATTERN.matcher(it).matches() }
                .map { it.substring(1) }  // v1.4.31 => 1.4.31
                .map { TfEnvToolVersion(it) }
                .toList()
            return FetchAvailableToolsResult.createSuccessful(tools)
        } catch (ex: Throwable) {
            val msg = "Failed to fetch available Kotlin compiler versions from $url"
            Loggers.SERVER.warnAndDebugDetails(msg, ex)
            return FetchAvailableToolsResult.createError(msg, ex)
        }
    }
}