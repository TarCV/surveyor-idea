fun properties(key: String) = providers.gradleProperty(key)
fun environment(key: String) = providers.environmentVariable(key)

plugins {
    // Java support
    id("java")
    // Kotlin support
    alias(libs.plugins.kotlin)
    // gradle-intellij-plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
    alias(libs.plugins.gradleIntelliJPlugin) apply false
    // gradle-changelog-plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
    alias(libs.plugins.changelog) apply false
    // Gradle Kover Plugin
    alias(libs.plugins.kover)

    `maven-publish` // for pom generation

    alias(libs.plugins.aar2jar) apply false
}

// Configure root project
tasks {
    wrapper {
        gradleVersion = properties("gradleVersion").get()
    }
}

allprojects {
    group = properties("pluginGroup").get()
    version = properties("pluginVersion").get()

    repositories {
        mavenCentral()
        maven {
            url = uri("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies")
            content {
                // Download only these groups from the repository
                includeGroup("com.intellij.remoterobot")
            }
        }
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
                includeGroup("com.github.beanshell")
                includeGroup("com.github.TarCV")
            }
        }
        google {
            metadataSources {
                // Redirection breaks aar2jar transformation
                ignoreGradleMetadataRedirection()
                mavenPom()
            }
        }
    }

    tasks {
        withType<AbstractArchiveTask>().configureEach {
            // Settings for reproducibility
            isPreserveFileTimestamps = false
            isReproducibleFileOrder = true
            filePermissions {
                unix("rw-r--r--")
            }
        }
    }
    afterEvaluate {
        tasks {
            withType<Jar> {
                manifest {
                    // Settings for reproducibility
                    attributes.replaceAll { k, v ->
                        when (k) {
                            "Build-JVM" -> {
                                v.toString()
                                    .replace(Regex("""(?<=[\.\+])\d+"""), "0")
                                    .replace(Regex("""(?<=\().+?(?=\d)"""), "_ ")
                                    .replace(Regex("""\D+(?=\))"""), "")
                                    .replace(Regex("""[+-][A-Za-z_]+"""), "")
                            }
                            "Build-OS" -> {
                                v.toString()
                                    .replace(Regex("""(?<=\.)\d+"""), "0")
                                    .replace(Regex("""[+-]\d+"""), "")
                                    .replace(Regex("""[+-][A-Za-z_]+"""), "")
                            }
                            else -> v
                        }
                    }
                }
                // Settings for reproducibility
            }
        }
    }
}
subprojects {
    apply(plugin = "maven-publish")
    apply(plugin = "kotlin")

    // Set the JVM language level used to build the project. Use Java 17 for 2022.2+.
    kotlin {
        jvmToolchain(17)
    }

    project.afterEvaluate {
        dependencies {
            implementation(platform(libs.junitBom))
            implementation(platform(libs.kotlinBom))
        }

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

// Configure Gradle Kover Plugin - read more: https://github.com/Kotlin/kotlinx-kover#configuration
kover {
    reports {
        total {
            xml {
                onCheck = true
            }
        }
    }
}
