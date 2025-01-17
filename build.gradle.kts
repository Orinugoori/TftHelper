// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    id("com.google.gms.google-services")version "4.4.2" apply false
}

buildscript {
    repositories {
        google() // Firebase 라이브러리를 위해 필수
        mavenCentral()
    }
    dependencies {
        classpath (libs.gradle)
        classpath (libs.google.services) // Firebase Services Plugin
    }
}
