plugins {
    kotlin("jvm") version "1.8.0"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation ("com.google.guava:guava:32.1.1-jre")
    implementation("org.elasticsearch.client:elasticsearch-rest-client:8.8.1")
    implementation ("co.elastic.clients:elasticsearch-java:8.8.1")
    implementation ("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.7.2")
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-android", "1.7.2")
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8", "1.7.2")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("MainKt")
}
