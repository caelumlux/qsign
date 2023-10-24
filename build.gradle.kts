@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
import net.mamoe.mirai.console.gradle.wrapNameWithPlatform

plugins {
    kotlin("jvm") version "1.8.0"

    `maven-publish`
    signing
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"

    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.22"
    id("net.mamoe.mirai-console") version "2.16.0"
    id("com.github.gmazzo.buildconfig") version "3.1.0"
}

group = "top.mrxiaom"
version = "1.2.1-beta"
findProperty("dev.sha")?.also {
    version = "$version-dev-$it"
}
val versionUnidbgFetchQSign = "Null"

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
    implementation(platform("net.mamoe:mirai-bom:2.16.0"))
    compileOnly("net.mamoe:mirai-core")
    compileOnly("net.mamoe:mirai-core-utils")
    compileOnly("net.mamoe:mirai-console-compiler-common")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.5.0")
    implementation("org.slf4j:jcl-over-slf4j:2.0.7")

    implementation(files("libs/unidbg-android-1.0.7.jar"))

}

tasks.test {
    useJUnitPlatform()
}

mirai {
    jvmTarget = JavaVersion.VERSION_1_8
}

java {
    withSourcesJar()
}

tasks {
    processResources {
        // temporary fix of mamoe/mirai#2478
        from(zipTree("libs/unidbg-android-1.0.7.jar"))
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
    javadoc {
        (options as StandardJavadocDocletOptions).run {
            locale("zh_CN")
            encoding("UTF-8")
            docEncoding("UTF-8")
            addBooleanOption("keywords", true)
            addBooleanOption("Xdoclint:none", true)
            addBooleanOption("html5", true)

            windowTitle = "QSign Javadoc"
            docTitle = "<b>QSign</b> $version"
        }
    }
    create<Jar>("javadocJar") {
        group = "documentation"
        dependsOn(javadoc)
        archiveClassifier.set("javadoc")
        from(javadoc.get().destinationDir)
    }
    create<net.mamoe.mirai.console.gradle.BuildMiraiPluginV2>("pluginJar") {
        group = "mirai"
        registerMetadataTask(
            this@tasks,
            "miraiPublicationPrepareMetadata".wrapNameWithPlatform(kotlin.target, true)
        )
        init(kotlin.target)
        destinationDirectory.value(
            project.layout.projectDirectory.dir(project.buildDir.name).dir("mirai")
        )
        archiveExtension.set("jar")
    }
}
publishing.publications {
    create("mavenRelease", MavenPublication::class) {
        groupId = "top.mrxiaom"
        artifactId = "qsign"
        version = rootProject.version.toString()

        artifact(tasks.named("sourcesJar"))
        artifact(tasks.named("javadocJar"))
        artifact(tasks.named("pluginJar"))

        pom {
            name.set("qsign")
            description.set("Get QQ sign with unidbg, but in mamoe/mirai.")
            url.set("https://github.com/MrXiaoM/qsign")
            licenses {
                license {
                    name.set("GPL-3.0 License")
                    url.set("https://www.gnu.org/licenses/gpl-3.0.html")
                }
            }
            developers {
                developer {
                    name.set("MrXiaoM")
                    email.set("mrxiaom@qq.com")
                }
            }
            scm {
                url.set("https://github.com/MrXiaoM/qsign")
                connection.set("scm:git:https://github.com/MrXiaoM/qsign.git")
                developerConnection.set("scm:git:https://github.com/MrXiaoM/qsign.git")
            }
        }
    }
}
signing {
    val signingKey = findProperty("signingKey")?.toString()
    val signingPassword = findProperty("signingPassword")?.toString()
    if (signingKey != null && signingPassword != null) {
        useInMemoryPgpKeys(signingKey, signingPassword)
    }
    sign(publishing.publications.getByName("mavenRelease"))
}
nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            findProperty("MAVEN_USERNAME")?.also { username.set(it.toString()) }
            findProperty("MAVEN_PASSWORD")?.also { password.set(it.toString()) }
        }
    }
}
