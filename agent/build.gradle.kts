import java.util.zip.*

plugins {
    kotlin("jvm")
    id("com.github.rodm.teamcity-agent")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":common"))
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.samskivert:jmustache:1.15")
}

task<Copy>("copyDependencies") {
    from(configurations.compileClasspath.get().resolve())
    into("build/libs")
}

teamcity {
    version = rootProject.extra["teamcityVersion"] as String

    agent {
        descriptor {
            pluginDeployment {
                useSeparateClassloader = true
            }
        }

        archiveName = "terraform-agent"

        files {
            into("lib") {
                from(configurations.implementation.get().allArtifacts.files)
            }
        }
    }
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