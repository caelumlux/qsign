plugins {
    kotlin("jvm")
    application
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.github.johnrengelman.shadow")
}

repositories {
    mavenCentral()
    maven("https://kotlin.bintray.com/ktor")
}
val ktorVersion = "2.3.1"

dependencies {
    implementation(rootProject)
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:1.4.12")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    implementation(files("../libs/unidbg-android-1.0.7.jar"))
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}

distributions {
    main {
        contents {
            from("../") {
                include("txlib/**")
            }
        }
    }
}
