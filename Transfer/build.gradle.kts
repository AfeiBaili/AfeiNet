plugins {
    kotlin("jvm")
}

val buildName = "transfer"
group = "cn.afeibaili.afeinet"
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
    file.writeText("java -cp \"${buildName}-${project.version}.jar;lib/*\" cn.afeibaili.afeinet.transfer.TransferMainKt --port=33393")
}

tasks.jar {
    archiveFileName.set("${buildName}-${project.version}.jar")
    destinationDirectory = layout.buildDirectory.dir("run")
    finalizedBy(copyDependencies, startBat)
}

val zip = tasks.register<Zip>("zip") {
    dependsOn(tasks.jar, copyDependencies, startBat)
    archiveFileName.set("${buildName}-${project.version}.zip")
    destinationDirectory = rootProject.layout.buildDirectory
    from(layout.buildDirectory.dir("run"))
}