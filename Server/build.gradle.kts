plugins {
    kotlin("jvm")
}

group = "cn.afeibaili"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(project.rootProject)
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}