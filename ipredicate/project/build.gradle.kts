plugins {
    `java-library`
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("reflect"))
    implementation(project(":library"))

    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation(libs.jqwik)
}

sourceSets.main {
    resources.srcDir("../licenses")
}
tasks.compileTestKotlin {
    kotlinJavaToolchain.toolchain.use(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(20))
    })
}
tasks.test {
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(20))
    })
    useJUnitPlatform {
        includeEngines.add("junit-jupiter")
        includeEngines.add("jqwik")
    }
    include("**/*.*")
}
