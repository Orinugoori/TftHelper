import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
}

val properties = Properties().apply {
    load(FileInputStream(rootProject.file("local.properties")))
}

android {
    namespace = "com.orinugoori.tfthelper"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.orinugoori.tfthelper"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug{
            manifestPlaceholders["APP_ID"] = properties["ADMOB_APP_ID"] as String
            manifestPlaceholders["AD_ID"] = "ca-app-pub-3940256099942544/1033173712" // 광고 테스트 ID
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            manifestPlaceholders["APP_ID"] = properties["ADMOB_APP_ID"] as String
            manifestPlaceholders["AD_ID"] = properties["ADMOB_AD_ID_TEST"] as String


        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.12"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}


dependencies {
    //google
    implementation (libs.play.services.ads)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)

    //coil
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)

    //Glide
    implementation(libs.glide)
    implementation(libs.landscapist.glide)
    

    // Core 및 Lifecycle 관련
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Retrofit
    implementation(libs.retrofit2.retrofit)
    implementation(libs.converter.gson)
    implementation (libs.logging.interceptor)


    // Jetpack Compose BOM
    implementation(platform(libs.androidx.compose.bom))

    // Compose 관련
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.pager.indicators)
    implementation(libs.accompanist.systemuicontroller)
    // Navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    // 테스트
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // 디버그
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    debugImplementation(libs.leakcanary.android)
}

