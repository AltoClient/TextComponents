plugins {
    kotlin("jvm")
    id("java-library")
}

repositories {
    mavenCentral()
    maven("https://libraries.minecraft.net")
}

dependencies {
    implementation("com.google.code.gson:gson:2.8.9")
}
