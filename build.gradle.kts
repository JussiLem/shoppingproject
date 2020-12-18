import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    kotlin("plugin.serialization") version "1.4.10"
    application
}

group = "me.jussi"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    mavenCentral()
    maven { url = uri("https://dl.bintray.com/kotlin/kotlinx") }
    maven { url = uri("https://dl.bintray.com/kotlin/ktor") }
}

val ktorVersion = "1.4.0"
fun ktor(module: String) = "io.ktor:ktor-$module:$ktorVersion"

dependencies {
    val junitVersion = "5.6.0"
    val assertJVersion = "3.17.2"
    testImplementation(kotlin("test-junit5"))
    implementation(kotlin("stdlib-jdk8"))
    implementation(ktor("server-cio"))
    implementation(ktor("serialization"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.assertj:assertj-core:$assertJVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.2")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("com.amazonaws:aws-java-sdk-dynamodb:1.11.919")
    implementation("io.reactivex.rxjava2:rxkotlin:2.4.0")

}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "13"
}

application {
    mainClassName = "shoppingproject.ApplicationKt"
}