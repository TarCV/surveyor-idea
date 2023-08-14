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

rootProject.name = "surveyor-idea"
include("plugin")

include("droid-selector")
include("droid-stubs")

include("library")
