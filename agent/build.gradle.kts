import java.util.zip.*

plugins {
    kotlin("jvm")
    id("com.github.rodm.teamcity-agent")
}

teamcity {
    version = rootProject.extra["teamcityVersion"] as String

    agent {
        descriptor {
            project.file("teamcity-agent-plugin.xml")
            pluginDeployment {
                useSeparateClassloader = true
            }
        }

        archiveName = "terraform-agent"
    }
}

dependencies {
    compile(
        kotlin("stdlib")
    )
    compile(
        project(":common")
    )
    compile("com.google.code.gson:gson:2.8.6")
    compile("com.samskivert:jmustache:1.15")
}

var mainClassName = "jetbrains.buildServer.terraformSupportPlugin.TerraformSupport"

tasks.withType<Jar> {
    baseName = "terraform-agent"


}

tasks["agentPlugin"].doLast {
    val zipTask = tasks["agentPlugin"] as Zip
    val zipFile = zipTask.archivePath

    val entries = zipFile.inputStream().use { it ->
        ZipInputStream(it).use { z ->
            generateSequence { z.nextEntry }
                .filterNot { it.isDirectory }
                .map { it.name }
                .toList()
                .sorted()
        }
    }
}