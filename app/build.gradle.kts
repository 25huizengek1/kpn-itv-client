plugins {
    kotlin("android")
    kotlin("plugin.serialization")
    id("com.android.application")
    id("com.google.devtools.ksp")
}

android {
    namespace = "me.huizengek.kpninteractievetv"
    compileSdk = 34

    defaultConfig {
        applicationId = "me.huizengek.kpninteractievetv"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        vectorDrawables {
            useSupportLibrary = true
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
        buildConfig = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + listOf("-Xcontext-receivers")
    }
}

kotlin {
    jvmToolchain(8)
}

ksp {
    arg("room.schemaLocation", projectDir.resolve("src/main/room/schemas").absolutePath)
}

dependencies {
    implementation(libs.core.ktx)
    implementation(platform(libs.compose.bom))
    implementation(libs.tv.foundation)
    implementation(libs.tv.material)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(libs.material)

    implementation(libs.destinations)
    ksp(libs.destinations.ksp)

    implementation(libs.room)
    implementation(libs.room.ktx)
    ksp(libs.room.ksp)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.json)

    implementation(libs.exoplayer)
    implementation(libs.exoplayer.dash)
    implementation(libs.exoplayer.session)
    implementation(libs.exoplayer.ui)

    implementation(libs.coil)
}