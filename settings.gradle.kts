pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
        maven {
            url = uri("https://jitpack.io")
            content {
                // Download only these groups from the repository
                includeGroup("com.github.TarCV")
                includeGroup("com.github.TarCV.aar2jar")
            }
        }
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "com.android.application") {
                useModule("com.android.tools.build:gradle:${requested.version}")
            }
            if (requested.id.id.startsWith("com.github.TarCV")) {
                val (_, _, _, name) = requested.id.id.split(".", limit = 4)
                useModule("com.github.TarCV.$name:build:${requested.version}")
            }
        }
    }
}
/*
TODO: Only disable during nix build
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.6.0"
}
*/

rootProject.name = "surveyor-idea"
include("plugin")
include("plugin-test")

include("droid-selector")
include("droid-stubs")

include("library")
