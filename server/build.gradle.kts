import com.github.rodm.teamcity.TeamCityEnvironment
import de.undercouch.gradle.tasks.download.Download

val BUNDLED_TFENV_TOOL_VERSION = "2.2.2"

plugins {
    kotlin("jvm")
    id("com.github.rodm.teamcity-server")
    id("de.undercouch.download")
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

        files {
            into("bundled") {
                from("$buildDir/bundled")
            }
        }
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

task<Download>("downloadBundled") {
    dependsOn("serverPlugin")

    src("https://github.com/tfutils/tfenv/archive/refs/tags/v${BUNDLED_TFENV_TOOL_VERSION}.zip")
    dest(
        mkdir("${buildDir}/bundled-download")
    )
}

task<Zip>("includeToolDef") {
    archiveFileName.set("tfenv.bundled.zip")
    destinationDirectory.set(file("$buildDir/bundled"))

    from(zipTree("$buildDir/bundled-download/v$BUNDLED_TFENV_TOOL_VERSION.zip")) {
        include("**")
        eachFile {
            relativePath = RelativePath(true, *relativePath.segments.drop(1).toTypedArray())
        }
        includeEmptyDirs = false
    }
    from("tools/teamcity-plugin.xml")
    dependsOn(
        "downloadBundled"
    )
}

task("teamcity") {
    dependsOn("serverPlugin")
    dependsOn("includeToolDef")

    doLast {
        println("##teamcity[publishArtifacts '${(tasks["serverPlugin"] as Zip).archiveFile}']")
    }
}