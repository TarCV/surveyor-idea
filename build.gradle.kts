import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    // Java support
    id("java")
    // Kotlin support
    id("org.jetbrains.kotlin.jvm") version "1.5.20"
    // gradle-intellij-plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
    id("org.jetbrains.intellij") version "1.3.0" apply false
    // gradle-changelog-plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
    id("org.jetbrains.changelog") version "1.1.2" apply false
    // detekt linter - read more: https://detekt.github.io/detekt/gradle.html
    id("io.gitlab.arturbosch.detekt") version "1.17.1"
    // ktlint linter - read more: https://github.com/JLLeitschuh/ktlint-gradle
    id("org.jlleitschuh.gradle.ktlint") version "10.1.0"

    `maven-publish` // for pom generation

    id("com.stepango.aar2jar") version "0.6" apply false
}

group = properties("pluginGroup")
version = properties("pluginVersion")

// Configure project's dependencies
allprojects {
    repositories {
        mavenCentral()
        google()
        maven { url = uri("https://jitpack.io") }
    }

    tasks {
        // Set the compatibility versions to 1.8
        withType<JavaCompile> {
            sourceCompatibility = "1.8"
            targetCompatibility = "1.8"
        }
        withType<KotlinCompile> {
            kotlinOptions.jvmTarget = "1.8"
            kotlinOptions.apiVersion = "1.3"
        }
    }
}
subprojects {
    apply(plugin = "maven-publish")
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
dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.17.1")
}

// Configure detekt plugin.
// Read more: https://detekt.github.io/detekt/kotlindsl.html
detekt {
    config = files("./detekt-config.yml")
    buildUponDefaultConfig = true

    reports {
        html.enabled = false
        xml.enabled = false
        txt.enabled = false
    }
}

tasks {
    withType<Detekt> {
        jvmTarget = "1.8"
    }
}
