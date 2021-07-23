package jetbrains.buildServer.terraformSupportPlugin.tfEnvTool

import jetbrains.buildServer.plugins.files.JarSearcherBase
import jetbrains.buildServer.tools.*
import jetbrains.buildServer.tools.available.AvailableToolsState
import jetbrains.buildServer.tools.available.AvailableToolsStateImpl
import jetbrains.buildServer.tools.available.DownloadableToolVersion
import jetbrains.buildServer.tools.available.FetchToolsPolicy
import jetbrains.buildServer.tools.utils.URLDownloader
import jetbrains.buildServer.util.*
import jetbrains.buildServer.util.positioning.PositionAware
import jetbrains.buildServer.util.positioning.PositionConstraint
import jetbrains.buildServer.util.ssl.SSLTrustStoreProvider
import jetbrains.buildServer.web.openapi.PluginDescriptor
import java.io.File
import java.io.IOException

class TfEnvToolProvider(val pluginDescriptor: PluginDescriptor,
                               val archiveManager: ArchiveExtractorManager,
                               val sslTrustStoreProvider: SSLTrustStoreProvider,
                               timeService: TimeService,
                               availableToolsFetcher: TfEnvAvailableToolsFetcher): PositionAware, ServerToolProviderAdapter() {

    private val availableTools: AvailableToolsState

    val TFENV_TOOL_BUNDLED_VERSION_NUMBER = "2.2.2"
    val TFENV_TOOL_BUNDLED_VERSION_ID = ToolVersionIdHelper.getToolId(TfEnvToolType.INSTANCE, "bundled")
    val DOT_ZIP = ".zip"

    init {
        availableTools = AvailableToolsStateImpl(timeService, listOf(availableToolsFetcher))
    }

    private var toolVersions: Map<String, DownloadableToolVersion> = emptyMap()
        get() {
            val result = availableTools
                .getAvailable(FetchToolsPolicy.ReturnCached)
                .fetchedTools.map { it.id to it }.toMap()
            if (!result.isEmpty()) field = result
            return field
        }

    private val bundledVersions by lazy {
        val pluginRoot = pluginDescriptor.pluginRoot.toPath()
        val path = pluginRoot.resolve("bundled").resolve(TFENV_TOOL_BUNDLED_VERSION_ID + DOT_ZIP)
        listOf(SimpleInstalledToolVersion(
            SimpleToolVersion(getType(), TFENV_TOOL_BUNDLED_VERSION_NUMBER, TFENV_TOOL_BUNDLED_VERSION_ID),
            null, null, path.toFile()))
    }

    override fun getType(): ToolType = TfEnvToolType.INSTANCE

    override fun getBundledToolVersions() = bundledVersions

    override fun getDefaultBundledVersionId() = TFENV_TOOL_BUNDLED_VERSION_ID

    override fun getAvailableToolVersions() = toolVersions.values

    override fun tryGetPackageVersion(toolPackage: File): GetPackageVersionResult {
        val zipName = toolPackage.name
        val versionNumber = zipName.substring(zipName.length - DOT_ZIP.length)
        val toolId = ToolVersionIdHelper.getToolId(TfEnvToolType.INSTANCE, versionNumber)
        val toolVersion = toolVersions.get(toolId) ?: TfEnvToolVersion(versionNumber)
        return GetPackageVersionResult.version(toolVersion)
    }

    @Throws(ToolException::class)
    override fun fetchToolPackage(toolVersion: ToolVersion, targetDirectory: File): File {
        val dowloadableVersion = toolVersions.get(toolVersion.id)
            ?: throw ToolException("Tool version ${toolVersion.id} not found")
        val location = File(targetDirectory, dowloadableVersion.getDestinationFileName())
        try {
            URLDownloader.download(dowloadableVersion.getDownloadUrl(), sslTrustStoreProvider.getTrustStore(), location)
        } catch (e: Throwable) {
            throw ToolException("Failed to download package " + toolVersion + " to " + location + e.message, e)
        }
        return location
    }

    @Throws(ToolException::class)
    override fun unpackToolPackage(toolPackage: File, targetDirectory: File) {
        try {
            archiveManager.extractFiles(toolPackage, ArchiveFileSelector {
                File(targetDirectory, it.substring(it.indexOf("/") + 1))
            })
            writeToolDescriptor(File(targetDirectory, JarSearcherBase.TEAMCITY_PLUGIN_XML))
        } catch (e: IOException) {
            throw ToolException("Error while unpacking a Kotlin compiler distribution: $e", e)
        } catch (e: UnsupportedArchiveTypeException) {
            throw ToolException("Unsupported archive format when trying to extract a Kotlin compiler tool from [" + toolPackage.absolutePath + "]", e)
        }
    }

    private fun writeToolDescriptor(file: File) {
        FileUtil.writeFile(file,
            """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <teamcity-agent-plugin xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                                           xsi:noNamespaceSchemaLocation="urn:shemas-jetbrains-com:teamcity-agent-plugin-v1-xml">
                      <tool-deployment />
                    </teamcity-agent-plugin>
                """.trimIndent(), "UTF-8")
    }

    override fun getConstraint() = PositionConstraint.first()
    // otherwise IntelliJServerToolProvider may try to parse the downloaded tool zips as well, see TW-70657

    override fun getOrderId() = TfEnvToolType.INSTANCE.type
}