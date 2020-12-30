plugins {
    id("com.github.rodm.teamcity-common") version "1.1" apply false
    id("com.github.rodm.teamcity-agent") version "1.1" apply false
    id("com.github.rodm.teamcity-server") version "1.1" apply false

    kotlin("jvm") version "1.4.10"
}

ext {
    set("teamcityVersion", "2020.2")
}

group = "jetbrains.buildserver"
version = System.getenv("BUILD_NUMBER") ?: "1.0-dev"


subprojects {

    repositories {
        mavenCentral()
        jcenter()
    }

    group = rootProject.group
    version = rootProject.version

    tasks.withType<Zip> {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}

task("teamcity") {
    // dependsOn(":tests:test")
    dependsOn(":server:teamcity")
}