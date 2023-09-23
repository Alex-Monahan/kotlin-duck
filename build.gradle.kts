plugins {
    kotlin("jvm") version "1.9.0"
    application
}

group = "nz.braveface"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    //implementation("org.duckdb:duckdb_jdbc:0.8.1")
    implementation("org.duckdb:duckdb_jdbc:0.9.0-patched")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}