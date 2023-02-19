import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    // Java support
    id("java")
    // Kotlin support
    id("org.jetbrains.kotlin.jvm") version "1.8.10"
    // gradle-intellij-plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
    id("org.jetbrains.intellij") version "1.13.0" apply false
    // gradle-changelog-plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
    id("org.jetbrains.changelog") version "2.0.0" apply false
    // Gradle Qodana Plugin
    id("org.jetbrains.qodana") version "0.1.13"
    // Gradle Kover Plugin
    id("org.jetbrains.kotlinx.kover") version "0.6.1"

    `maven-publish` // for pom generation

    id("com.stepango.aar2jar") version "0.6" apply false
}

group = properties("pluginGroup")
version = properties("pluginVersion")

// Configure root project
tasks {
    wrapper {
        gradleVersion = properties("gradleVersion")
    }
}

allprojects {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
            content {
                // Download only these groups from the repository
                includeGroup("org.beanshell")
            }
        }
        maven {
            url = uri("https://jitpack.io")
            content {
                // Download only these groups from the repository
                includeGroup("com.github.TarCV.beanshell")
            }
        }
        google()
    }

    tasks {
        // Set the JVM compatibility versions
        withType<KotlinCompile> {
            kotlinOptions.apiVersion = properties("kotlinApiVersion")
        }
    }
}
subprojects {
    apply(plugin = "maven-publish")
    apply(plugin = "kotlin")

    // Set the JVM language level used to build the project. Use Java 11 for 2020.3+, and Java 17 for 2022.2+.
    kotlin {
        jvmToolchain(11)
    }

    project.afterEvaluate {
        publishing {
            publications {
                artifacts {
                    create<MavenPublication>("artifact") {
                        from(components["java"])
                    }
                }
            }
        }

        task("copyPomForCi", Sync::class) {
            dependsOn("generatePomFileForArtifactPublication")
            from("build/publications/artifact")
            into("${rootProject.projectDir}/ci/poms/${project.projectDir.name}")
            include("pom-default.xml")
            rename("pom-default.xml", "pom.xml")
        }
    }
}

// Configure Gradle Qodana Plugin - read more: https://github.com/JetBrains/gradle-qodana-plugin
qodana {
    cachePath.set(file(".qodana").canonicalPath)
    reportPath.set(file("build/reports/inspections").canonicalPath)
    saveReport.set(true)
    showReport.set(System.getenv("QODANA_SHOW_REPORT")?.toBoolean() ?: false)
}

// Configure Gradle Kover Plugin - read more: https://github.com/Kotlin/kotlinx-kover#configuration
kover.xmlReport {
    onCheck.set(true)
}
