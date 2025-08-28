dependencies {
    implementation(project(":types"))

    implementation("com.michael-bull.kotlin-inline-logger:kotlin-inline-logger-jvm:${findProperty("inlineLoggingVersion")}")
    implementation("com.google.devtools.ksp:symbol-processing-api:${findProperty("kspVersion")}")
    implementation("com.squareup:kotlinpoet:${findProperty("kotlinPoetVersion")}")
    implementation("com.squareup:kotlinpoet-ksp:${findProperty("kotlinPoetVersion")}")

    testImplementation("org.junit.jupiter:junit-jupiter-params:${findProperty("junitVersion")}")
}
