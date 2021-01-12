import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    `maven-publish`
    kotlin("jvm") version "1.4.30-M1"
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://jitpack.io")
    }

    maven {
        url = uri("https://jcenter.bintray.com")
    }

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    mavenCentral()
}

dependencies {
    implementation("net.dv8tion:JDA:4.2.0_225")
    implementation("org.apache.maven.plugins:maven-compiler-plugin:3.8.1")
    implementation("org.slf4j:slf4j-simple:1.7.12")
    implementation("org.json:json:20180130")
    implementation("com.github.ReflxctionDev:SimpleHypixelAPI:8eff50e")
    implementation("com.googlecode.json-simple:json-simple:1.1.1")
    implementation("org.apache.commons:commons-lang3:3.11")
    implementation("redis.clients:jedis:3.4.1")
    implementation("com.github.Lulonaut:HypixelAPIWrapper:d7e93b2f9c")
    implementation(kotlin("stdlib-jdk8"))
}

group = "de.lulonaut"
version = "0.123"
description = "JDABot"
java.sourceCompatibility = JavaVersion.VERSION_1_8

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "de.lulonaut.bot.Main"
    }

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}