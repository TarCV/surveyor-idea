import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML

fun properties(key: String) = providers.gradleProperty(key)
fun environment(key: String) = providers.environmentVariable(key)

plugins {
    kotlin("jvm")
    alias(libs.plugins.aar2jar)

    // gradle-intellij-plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
    id("org.jetbrains.intellij")
    // gradle-changelog-plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
    id("org.jetbrains.changelog")
}

dependencies {
    implementation(kotlin("reflect"))
    implementation(libs.errorprone)

    implementation(project(":droid-selector")) {
        exclude(group = "org.jetbrains.kotlin")
    }
    implementation(project(":ipredicate")) {
        exclude(group = "org.jetbrains.kotlin")
    }
    implementation(project(":library")) {
        exclude(group = "org.jetbrains.kotlin")
    }

    // This is required because of some quirks of plugins classloaders
    implementation(libs.uiautomator) {
        // Workaround 'implementationAar' configuration not being used when building a plugin
        attributes {
            attribute(
                Attribute.of("artifactType", String::class.java),
                ArtifactTypeDefinition.JAR_TYPE
            )
        }

        isTransitive = false
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xjvm-default=all")
    }
}

// Configure Gradle IntelliJ Plugin - read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    intellijRepository = properties("NIX_GRADLE_DEPS_1").map { "file://${it}" }.orNull

    pluginName = properties("pluginName")
    version = properties("platformVersion")
    type = properties("platformType")

    // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file.
    plugins = properties("platformPlugins").map { it.split(',').map(String::trim).filter(String::isNotEmpty) }

    downloadSources = true
}

// Configure Gradle Changelog Plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
changelog {
    groups.empty()
    path = rootProject.layout.projectDirectory.file("CHANGELOG.md").toString()
    repositoryUrl = properties("pluginRepositoryUrl")
}

// Prevent downloading IDE jars as there is no unit/integration tests in this module:
gradle.startParameter.excludedTaskNames.add(":plugin:test")

tasks {
    downloadRobotServerPlugin {
        version.set(libs.versions.remoteRobot) // otherwise the current latest version is used which is bad for reproducibility
    }

    buildSearchableOptions {
        // Remove once some settings are added
        enabled = false
        notCompatibleWithConfigurationCache("Configuration cache for this task is broken on NixOS")
    }

    runPluginVerifier {
        distributionFile = properties("pluginDistributionFile").map { file(it) }.orNull
    }

    patchPluginXml {
        version = properties("pluginVersion")
        sinceBuild = properties("pluginSinceBuild")
        untilBuild = properties("pluginUntilBuild")

        // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
        pluginDescription = providers.fileContents(rootProject.layout.projectDirectory.file("README.md")).asText.map {
            val start = "<!-- Plugin description -->"
            val end = "<!-- Plugin description end -->"

            with (it.lines()) {
                if (!containsAll(listOf(start, end))) {
                    throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                }
                subList(indexOf(start) + 1, indexOf(end)).joinToString("\n").let(::markdownToHTML)
            }
        }

        // local variables for configuration cache compatibility:
        val changelog = project.changelog
        // Get the latest available change notes from the changelog file
        changeNotes = properties("pluginVersion").map { pluginVersion ->
            with(changelog) {
                renderItem(
                    (getOrNull(pluginVersion) ?: getUnreleased())
                        .withHeader(false)
                        .withEmptySections(false),
                    Changelog.OutputType.HTML,
                )
            }
        }
    }

    signPlugin {
        certificateChain = environment("CERTIFICATE_CHAIN")
        privateKey = environment("PRIVATE_KEY")
        password = environment("PRIVATE_KEY_PASSWORD")
    }

    publishPlugin {
        dependsOn("patchChangelog")
        token = environment("PUBLISH_TOKEN")
        // The pluginVersion is based on the SemVer (https://semver.org) and supports pre-release labels, like 2.1.7-alpha.3
        // Specify pre-release label to publish the plugin in a custom Release Channel automatically. Read more:
        // https://plugins.jetbrains.com/docs/intellij/deployment.html#specifying-a-release-channel
        channels = properties("pluginVersion").map { listOf(it.substringAfter('-', "").substringBefore('.').ifEmpty { "default" }) }
    }
}
