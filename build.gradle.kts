plugins {
    kotlin("plugin.serialization") version "1.5.10"
    kotlin("jvm") version "1.9.23"
}

group = "com.tsepesh.thoma"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}


dependencies {
    testImplementation(kotlin("test"))
    implementation("com.google.apis:google-api-services-sheets:v4-rev581-1.25.0")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.23.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1-Beta")
    implementation("org.jsoup:jsoup:1.15.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")

}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(20)
}