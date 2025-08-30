dependencies {
    implementation(project(":types"))
    implementation(project(":engine"))

    implementation("com.michael-bull.kotlin-inline-logger:kotlin-inline-logger-jvm:${findProperty("inlineLoggingVersion")}")
    implementation("com.google.devtools.ksp:symbol-processing-api:${findProperty("kspVersion")}")
    implementation("com.squareup:kotlinpoet:${findProperty("kotlinPoetVersion")}")
    implementation("com.squareup:kotlinpoet-ksp:${findProperty("kotlinPoetVersion")}")
    implementation(kotlin("reflect:1.9.22"))

    testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.5.0")
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing-ksp:1.5.0")
    testImplementation("io.mockk:mockk:${findProperty("mockkVersion")}")
}
