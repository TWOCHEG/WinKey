// я... не видел света уже так долго
plugins {
    id("fabric-loom") version "1.9-SNAPSHOT"
    id("maven-publish")
    id("org.jetbrains.kotlin.jvm") version "2.2.21"
    // id("org.jetbrains.kotlin.plugin.serialization") version "2.2.21"
}

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://maven.meteordev.org/releases")
    maven("https://libraries.minecraft.net")
}

version = project.property("mod_version") as String
group = project.property("maven_group") as String

base {
    archivesName.set(project.property("archives_base_name") as String)
}

val minecraftVersion: String by project
val yarnMappings: String by project
val loaderVersion: String by project
val fabricVersion: String by project
val fabricKotlinVersion: String by project

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings("net.fabricmc:yarn:$yarnMappings:v2")
    implementation("net.fabricmc:fabric-loader:$loaderVersion")

    implementation("net.fabricmc.fabric-api:fabric-api:$fabricVersion")

    implementation("net.fabricmc:fabric-language-kotlin:${fabricKotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("meteordevelopment:orbit:0.2.3")

    implementation("com.google.code.gson:gson:2.8.9")

    implementation("net.fabricmc:dev-launch-injector:0.2.1+build.8")
}

tasks.processResources {
    inputs.property("version", project.version)
    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(22)
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_22
    targetCompatibility = JavaVersion.VERSION_22
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${base.archivesName.get()}" }
    }
}

publishing {
    publications {
        register<MavenPublication>("maven") {
            from(components["java"])

            artifact(tasks.remapJar.get())
            artifact(tasks.named("sourcesJar").get())
        }
    }

    repositories {
        // mavenLocal()
    }
}