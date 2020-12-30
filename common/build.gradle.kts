plugins {
    kotlin("jvm")
    id("com.github.rodm.teamcity-common")
}

dependencies {
    compile(kotlin("stdlib"))
}


teamcity {
    version = rootProject.extra["teamcityVersion"] as String
}


tasks.withType<Jar> {
    baseName = "terraform-common"
}