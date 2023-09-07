plugins {
    kotlin("jvm") version "1.9.0"
    application
}

group = "nz.braveface"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    // implementation("org.duckdb:duckdb_jdbc:0.8.1")
    implementation(files("/Users/alex/Documents/DuckDB/duckdb_motherduck/build/release/tools/jdbc/duckdb_jdbc.jar"))
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(20) // Needed for buffer position method to exist
}

application {
    mainClass.set("MainKt")
}