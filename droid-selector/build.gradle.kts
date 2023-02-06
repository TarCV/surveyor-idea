import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
val junitJupiterVersion = "5.7.1"
val jqwikVersion = "1.5.0"

plugins {
    java
    kotlin("jvm")
    id("com.stepango.aar2jar")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.slf4j:slf4j-log4j12:1.7.30")

    implementation(project(":library"))
    implementation(project(":droid-stubs"))
    implementation("org.beanshell:bsh:3.0.0-20230205" +
            ".094654-3")
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
