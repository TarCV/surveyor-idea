import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm")
    id("com.github.TarCV.aar2jar")
}

dependencies {
    implementation(project(":library"))
    api(project(":droid-common"))
    implementation(project(":droid-stubs"))
    implementation(libs.beanshell)
    implementationAar(libs.uiautomator) {
        isTransitive = false
    }
    testImplementationAar(libs.uiautomator) {
        isTransitive = false
    }

    testImplementation(libs.junitApi)
    testRuntimeOnly(libs.junitEngine)
    testRuntimeOnly(libs.junitLauncher)
    testImplementation(libs.jqwik)
    testImplementation(kotlin("reflect"))
}

tasks.forEach { task ->
    if (task is KotlinCompile) {
        task.kotlinOptions {
            freeCompilerArgs += listOf("-Xinline-classes")
        }
    }
}

tasks.test {
    useJUnitPlatform {
        includeEngines.add("junit-jupiter")
        includeEngines.add("jqwik")
    }
    include("**/*.*")
}
