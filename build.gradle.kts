plugins {
    kotlin("jvm") version "2.2.20"
}

group = "cn.afeibaili"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    api("io.netty:netty-all:4.1.138.Final")
    api("tools.jackson.core:jackson-databind:3.2.4")
    api("tools.jackson.module:jackson-module-kotlin:3.2.4")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}