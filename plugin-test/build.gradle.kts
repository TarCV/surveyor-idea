plugins {
    kotlin("jvm")
}

dependencies {
    testImplementation("commons-io:commons-io:2.15.0")
    testImplementation("org.apache.commons:commons-text:1.11.0")

    testImplementation(kotlin("test"))
    testImplementation(libs.junitApi)
    testImplementation(libs.junitParams)
    testRuntimeOnly(libs.junitEngine)
    testRuntimeOnly(libs.junitLauncher)

    testImplementation(libs.remoteRobot) {
        exclude(group = "junit", module = "junit") // exclude JUnit 4 not used in the project
    }
    testImplementation(libs.remoteRobotFixtures) {
        exclude(group = "junit", module = "junit") // exclude JUnit 4 not used in the project
    }
    testImplementation(libs.remoteRobotLauncher) {
        exclude(group = "junit", module = "junit") // exclude JUnit 4 not used in the project
    }
    testImplementation(libs.junitVideo)
}

tasks {
    test {
        doNotTrackState("UI tests should always run")

        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}
