plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.secrets)
    alias(libs.plugins.ksp)
}

android {
    namespace = "io.github.ivan8m8.courierhelper"
    compileSdk = 35

    defaultConfig {
        applicationId = "io.github.ivan8m8.courierhelper"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.adapterdelegates)
    implementation(libs.adapterdelegates.viewbinding)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.preference)
    implementation(libs.androidx.room)
    ksp           (libs.androidx.room.compiler)
    implementation(libs.androidx.room.rxjava3)
    implementation(libs.androidx.workmanager)
    implementation(libs.androidx.workmanager.rxjava3)
    implementation(libs.koin)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.workmanager)
    implementation(libs.material)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.osmdroid)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson.converter)
    implementation(libs.retrofit.rxjava3.adapter)
    implementation(libs.rxandroid)
    implementation(libs.rxjava3)
    implementation(libs.rxkotlin)
    implementation(libs.rxrelay)
    implementation(libs.timber)

    debugImplementation(libs.leakcanary)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso)
}