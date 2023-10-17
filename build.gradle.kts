// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {

    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
        }
    }
    dependencies {
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.21")
        classpath ("com.google.dagger:hilt-android-gradle-plugin:2.48")
        classpath ("com.google.gms:google-services:4.4.0")

    }
}

plugins {
    id("com.android.application") version "7.4.2" apply false
    id("com.android.library") version "7.4.2" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    kotlin("kapt") version "1.8.10"
}


tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

