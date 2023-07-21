plugins {
    kotlin("jvm") version "1.8.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.22"
    id("net.mamoe.mirai-console") version "2.15.0"
    id("com.github.gmazzo.buildconfig") version "3.1.0"
}

group = "top.mrxiaom"
version = "1.0.4"
val versionUnidbgFetchQSign = "1.1.6"

buildConfig {
    className("BuildConstants")
    packageName("top.mrxiaom.qsign")
    useKotlinOutput()

    buildConfigField("String", "VERSION", "\"${project.version}\"")
    buildConfigField("String", "UNIDBG_FETCH_QSIGN_VERSION", "\"$versionUnidbgFetchQSign\"")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(platform("net.mamoe:mirai-bom:2.15.0"))
    compileOnly("net.mamoe:mirai-core")
    compileOnly("net.mamoe:mirai-core-utils")
    compileOnly("net.mamoe:mirai-console-compiler-common")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.3.3")
    implementation("org.slf4j:jcl-over-slf4j:2.0.7")

    implementation(files("libs/unidbg-1.0.2.jar"))

}

tasks.test {
    useJUnitPlatform()
}

mirai {
    jvmTarget = JavaVersion.VERSION_11
}

tasks {
    processResources {
        // temporary fix of mamoe/mirai#2478
        from(zipTree("libs/unidbg-1.0.2.jar"))
    }
    create<Zip>("deploy") {
        group = "build"
        dependsOn("buildPlugin")
        setMetadataCharset("utf-8")
        from(fileTree("txlib")) {
            into("txlib")
        }
        from("build/mirai/${rootProject.name}-${rootProject.version}.mirai2.jar") {
            into("plugins")
        }
        destinationDirectory.set(rootProject.projectDir)
        archiveFileName.set("${rootProject.name}-${rootProject.version}-all.zip")
    }
}