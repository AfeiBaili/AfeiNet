plugins {
    kotlin("jvm")
}

val buildName = "server"
group = "cn.afeibaili.afeinet.server"
version = "1.0.0"

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

val copyDependencies by tasks.register("copyDependencies", Copy::class.java) {
    from(configurations.runtimeClasspath)
    into(layout.buildDirectory.dir("run/lib"))
}

val startBat by tasks.registering {
    val file: File = layout.buildDirectory.file("run/run.bat").get().asFile
    file.parentFile.mkdirs()
    file.createNewFile()
    file.writeText("java -cp \"${buildName}-${project.version}.jar;lib/*\" cn.afeibaili.afeinet.server.ServerMainKt")
}

tasks.jar {
    archiveFileName.set("${buildName}-${project.version}.jar")
    destinationDirectory = layout.buildDirectory.dir("run")
    finalizedBy(copyDependencies, startBat)
}
