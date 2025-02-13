plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    kotlin("plugin.serialization") version "1.9.20"
    `maven-publish`
}

android {
    namespace = "com.earldev.self_update"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.earldev.lib"
            artifactId = "self_update"
            version = "1.0.0-RC"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}

dependencies {
    // Core
    api(libs.kotlin.stdlib)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // OkHttp
    implementation(libs.okhttp)
    implementation(libs.okhttp3.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.kotlinx.serialization.json)

    // Dagger2
    implementation(libs.dagger)
    kapt(libs.dagger.compiler)
}