import java.util.zip.ZipInputStream

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
    compile("com.fasterxml.jackson.core:jackson-databind:2.12.4")
    compile("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.4")
    compile("io.pebbletemplates:pebble:3.1.5")
    compile("com.google.guava:guava:30.1.1-jre")
}

configurations.all {
    exclude(group = "org.slf4j")
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