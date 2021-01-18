import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.4.30-M1"
    id("com.github.johnrengelman.shadow") version "4.0.4"
}



repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
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
}

dependencies {
    implementation("net.dv8tion:JDA:4.2.0_225")
    implementation("org.apache.maven.plugins:maven-compiler-plugin:3.8.1")
    implementation("org.slf4j:slf4j-simple:1.7.12")
    implementation("org.json:json:20180130")
    implementation("com.github.ReflxctionDev:SimpleHypixelAPI:8eff50e")
    implementation("org.apache.commons:commons-lang3:3.11")
    implementation("redis.clients:jedis:3.4.1")
    implementation("com.github.Lulonaut:HypixelAPIWrapper:d7e93b2f9c")
    implementation(kotlin("stdlib-jdk8"))
}

group = "de.lulonaut"
version = "0.123"
description = "JDABot"
java.sourceCompatibility = JavaVersion.VERSION_1_8


val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
tasks {
    named<ShadowJar>("shadowJar") {
        archiveBaseName.set("shadow")
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to "de.lulonaut.bot.Main"))
        }
    }
}

tasks {
    build { dependsOn(shadowJar) }

}