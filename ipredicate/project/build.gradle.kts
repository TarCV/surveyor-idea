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
tasks.compileTestJava {
    javaCompiler.set(javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(21))
    })
    options.compilerArgs.add("--enable-preview")
}
tasks.compileTestKotlin {
    kotlinJavaToolchain.toolchain.use(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(21))
    })
    compilerOptions.freeCompilerArgs.add("-Xjvm-enable-preview")
}
tasks.test {
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(21))
    })
    useJUnitPlatform {
        includeEngines.add("junit-jupiter")
        includeEngines.add("jqwik")
    }
    jvmArgs(
        "--enable-preview",
        providers.environmentVariable("ICU_HOME")
            .orElse("/nix/store/cxw0yh9fa41wzhhm51dv97annri01lid-icu4c-70.1")
            .map { "-Djava.library.path=$it/lib" }
            .get()
    )
    include("**/*.*")
}
