plugins {
    java
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation(project(":droid-common"))
    implementation(project(":library"))

    testImplementation(libs.junitApi)
    testRuntimeOnly(libs.junitEngine)
    testRuntimeOnly(libs.junitLauncher)
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
