import org.gradle.configurationcache.extensions.serviceOf
import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.intellij.jbr.JbrResolver
import org.jetbrains.intellij.logCategory
import org.jetbrains.kotlin.gradle.internal.ensureParentDirsCreated

fun properties(key: String) = providers.gradleProperty(key)
fun environment(key: String) = providers.environmentVariable(key)

val remoteRobotVersion = "0.11.18"

plugins {
    kotlin("jvm")
    id("com.github.TarCV.aar2jar")

    // gradle-intellij-plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
    id("org.jetbrains.intellij")
    // gradle-changelog-plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
    id("org.jetbrains.changelog")
}

dependencies {
    implementation(project(":droid-selector")) {
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

    testImplementation(platform("org.junit:junit-bom:5.7.1"))
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.1")

    testImplementation("com.intellij.remoterobot:remote-robot:$remoteRobotVersion") {
        exclude(group = "junit", module = "junit") // exclude JUnit 4 not used in the project
    }
    testImplementation("com.intellij.remoterobot:remote-fixtures:$remoteRobotVersion") {
        exclude(group = "junit", module = "junit") // exclude JUnit 4 not used in the project
    }
    testImplementation("com.automation-remarks:video-recorder-junit5:2.0")
}

// Configure Gradle IntelliJ Plugin - read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    intellijRepository = properties("NIX_GRADLE_DEPS_1").map { "file://${it}" }.orNull

    pluginName = properties("pluginName")
    version = properties("platformVersion")
    type = properties("platformType")

    // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file.
    plugins = properties("platformPlugins").map { it.split(',').map(String::trim).filter(String::isNotEmpty) }
}

// Configure Gradle Changelog Plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
changelog {
    groups.empty()
    path = rootProject.layout.projectDirectory.file("CHANGELOG.md").toString()
    repositoryUrl = properties("pluginRepositoryUrl")
}


tasks {
    test {
        doNotTrackState("UI tests should always run")
        systemProperty("idea.split.test.logs", true)

        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    downloadRobotServerPlugin {
        version.set(remoteRobotVersion) // otherwise the current latest version is used which is bad for reproducibility
    }

    buildSearchableOptions {
        // Remove once some settings are added
        enabled = false
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

    prepareUiTestingSandbox {
        finalizedBy("finalizeUiTestingSandbox")
    }
    register("finalizeUiTestingSandbox") {
        val disablePluginPathProperty = prepareUiTestingSandbox.get()
            .configDir.map { file(it).resolve("disabled_plugins.txt") }
        outputs.file(disablePluginPathProperty)
        doLast {
            val disablePluginPath = disablePluginPathProperty.get()
            disablePluginPath.ensureParentDirsCreated()
            disablePluginPath.writeText(buildString {
                // Disable 'Code with Me' tooltip:
                appendLine("com.jetbrains.codeWithMe")
            })
        }
    }

    // Configure UI tests plugin
    // Read more: https://github.com/JetBrains/intellij-ui-test-robot
    runIdeForUiTests {
/*        jbrVariant.set(
            environment("CI")
                .map {
                    if (it.isNotEmpty()) {
                        ""
                    } else {
                        "sdk"
                    }
                }
        )*/
        systemProperty("robot-server.port", "8082")
        systemProperty("ide.mac.message.dialogs.as.sheets", "false")
        systemProperty("ide.show.tips.on.startup.default.value", false)
        systemProperty("idea.trust.all.projects", "true")
        systemProperty("jb.consents.confirmation.enabled", "false")
        systemProperty("jb.privacy.policy.text", "<!--999.999-->")

        // Disable native menus on Mac:
        systemProperty("apple.laf.useScreenMenuBar", false)
        systemProperty("jbScreenMenuBar.enabled", false)
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
        channels = properties("pluginVersion").map { listOf(it.split('-').getOrElse(1) { "default" }.split('.').first()) }
    }
}