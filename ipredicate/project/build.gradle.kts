plugins {
    `java-library`
    kotlin("jvm")
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(kotlin("reflect"))
    implementation(project(":library"))
    implementation(libs.u4jregex)

    testImplementation(libs.junitApi)
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation(libs.jqwik)
}

sourceSets.main {
    kotlin.srcDir(
        // Provide generated sources for CI jobs that doesn't run `nix build`
        providers.gradleProperty("NIX_GRADLE_DEPS_1")
            .map { "__should_not_exist__" }
            .orElse("${rootProject.projectDir}/ci/generated/${project.projectDir.name}")
    )
    resources.srcDir("../licenses")
}

tasks.test {
    doNotTrackState("JQwik tests should always run")
    useJUnitPlatform {
        includeEngines.add("junit-jupiter")
        includeEngines.add("jqwik")
    }
    include("**/*.*")
}
tasks.register<Sync>("copyGeneratedSourceForCi") {
    from("${rootProject.projectDir}/result")
    into("${rootProject.projectDir}/ci/generated/${project.projectDir.name}")
    include("GSPredicate.kt", "WebDriverAgent.kt")
    filePermissions {
        unix("644")
    }
}