

plugins {
    application
    kotlin("plugin.serialization") version "1.9.23"
    kotlin("jvm") version "1.9.23"  // Замените на вашу версию Kotlin
}

group = "com.tsepesh.thoma"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven {
        name = "Sonatype Snapshots (Legacy)"
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
    maven {
        name = "Sonatype Snapshots"
        url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots")
    }
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.google.apis:google-api-services-sheets:v4-rev581-1.25.0")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.23.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1-Beta")
    implementation("org.jsoup:jsoup:1.15.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")
    implementation("dev.kord:kord-core:0.13.1")
    implementation("com.kotlindiscord.kord.extensions:kord-extensions:1.8.0-SNAPSHOT")  // Добавьте эту строку
    implementation("org.slf4j:slf4j-simple:2.0.9")  // Добавьте эту строку
}

application{
    mainClass.set("com.tsepesh.thoma.bot.SWHelperBot")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(20)
}
