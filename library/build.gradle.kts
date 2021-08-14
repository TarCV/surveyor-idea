plugins {
    java
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.slf4j:slf4j-log4j12:1.7.30")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}