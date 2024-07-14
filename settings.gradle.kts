pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
        maven {
            url = uri("https://jitpack.io")
            content {
                // Download only these groups from the repository
                includeGroup("com.github.stepango.aar2jar")
            }
        }
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "com.android.application") {
                useModule("com.android.tools.build:gradle:${requested.version}")
            }
            if (requested.id.id.startsWith("com.stepango.aar2jar")) {
                val (_, _, name) = requested.id.id.split(".", limit = 3)
                useModule("com.github.stepango.$name:build:${requested.version}")
            }
        }
    }
}

rootProject.name = "surveyor-idea"
include("plugin")
include("plugin-test")

include("droid-common")
include("droid-selector")
include("droid-stubs")

include("ipredicate")
project(":ipredicate").projectDir = file("ipredicate/project")

include("library")
