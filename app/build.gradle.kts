plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = ConfigData.compileSdk
    buildToolsVersion = ConfigData.buildTools

    defaultConfig {
        applicationId = "com.rabross.rabble"
        minSdk = ConfigData.minSdk
        targetSdk = ConfigData.targetSdk
        versionCode = ConfigData.versionCode
        versionName = ConfigData.versionName
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.compose
    }
}

dependencies {
    implementation(project(":game"))
    implementation(Dependencies.hilt)
    implementation(Dependencies.compose_runtime)
    implementation(Dependencies.compose_ui)
    debugImplementation(Dependencies.compose_ui_tooling)
    implementation(Dependencies.compose_ui_tooling_preview)
    implementation(Dependencies.compose_material)
    implementation(Dependencies.activity_compose)
    implementation(Dependencies.lifecycle_runtime_ktx)
    kapt(Dependencies.hilt_compiler)
}

kapt {
    correctErrorTypes = true
}