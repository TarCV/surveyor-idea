plugins {
    java
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation(project(":library"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
