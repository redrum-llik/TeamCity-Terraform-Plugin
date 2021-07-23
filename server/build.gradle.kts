import com.github.rodm.teamcity.TeamCityEnvironment

val BUNDLED_TFENV_TOOL_VERSION = "2.2.2"

plugins {
    kotlin("jvm")
    id("com.github.rodm.teamcity-server")
}

dependencies {
    compile(kotlin("stdlib"))
    compile(project(":common"))

    ///for BuildProblemManager
    compileOnly("org.jetbrains.teamcity.internal:server:${rootProject.ext["teamcityVersion"]}")
    compileOnly("org.jetbrains.teamcity.internal:server-tools:${rootProject.extra["teamcityVersion"]}")

    agent(project(path = ":agent", configuration = "plugin"))
}

teamcity {
    // Use TeamCity 8.1 API
    version = rootProject.ext["teamcityVersion"] as String

    server {
        descriptor = file("teamcity-plugin.xml")
        tokens = mapOf("Version" to rootProject.version)
        archiveName = "terraform-plugin"
    }

    environments {
        operator fun String.invoke(block: TeamCityEnvironment.() -> Unit) {
            environments.create(this, closureOf(block))
        }

        "teamcity" {
            version = rootProject.ext["teamcityVersion"] as String
        }
    }
}


tasks.withType<Jar> {
    baseName = "terraform-plugin"
}

abstract class DownloadTfEnvTask : DefaultTask() {
    @get:Input
    abstract var toolVersion: String

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun download() {
        val destFile = outputDir.file("tfenv-$toolVersion.zip").get().asFile
        if (!destFile.exists()) {
            val url = "https://github.com/tfutils/tfenv/archive/refs/tags/v${version}.zip"
            ant.invokeMethod("get", mapOf("src" to url, "dest" to destFile))
        }
    }
}

tasks {
    register<DownloadTfEnvTask>("downloadBundled") {
        toolVersion = BUNDLED_TFENV_TOOL_VERSION
        outputDir.set(File("$buildDir/bundled-download"))
    }

    register<Zip>("includeToolDef") {
        archiveFileName.set("tfenv.bundled.zip")
        destinationDirectory.set(file("$buildDir/bundled"))

        from(zipTree("$buildDir/bundled-download/tfenv-$BUNDLED_TFENV_TOOL_VERSION.zip")) {
            include("**")
            eachFile {
                relativePath = RelativePath(true, *relativePath.segments.drop(1).toTypedArray())
            }
            includeEmptyDirs = false
        }
        from("tools/teamcity-plugin.xml")
        dependsOn(named("downloadBundled"))
    }
}

task("teamcity") {
    dependsOn("serverPlugin")

    doLast {
        println("##teamcity[publishArtifacts '${(tasks["serverPlugin"] as Zip).archiveFile}']")
    }
}