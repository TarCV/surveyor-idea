import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
val junitJupiterVersion = "5.9.3"
val jqwikVersion = "1.5.0"

plugins {
    java
    kotlin("jvm")
    id("com.stepango.aar2jar")
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation(project(":library"))
    implementation(project(":droid-stubs"))
    implementation("com.github.TarCV.beanshell:bsh:5e2cc2f240eaec2d7d9c9b745aa3a5d04bc8fa2c")
    implementationAar("androidx.test.uiautomator:uiautomator:2.2.0") {
        isTransitive = false
    }
    testImplementationAar("androidx.test.uiautomator:uiautomator:2.2.0") {
        isTransitive = false
    }

    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitJupiterVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitJupiterVersion}")
    testImplementation("net.jqwik:jqwik:${jqwikVersion}")
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
