plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.android.application)
    alias(libs.plugins.ksp)
}

android {
    namespace = project.group.toString()
    compileSdk = 34

    defaultConfig {
        applicationId = project.group.toString()
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = project.version.toString()

        vectorDrawables.useSupportLibrary = true
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
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
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

composeCompiler {
    enableStrongSkippingMode = true
}

kotlin {
    jvmToolchain(libs.versions.jvm.get().toInt())
}

ksp {
    arg("room.schemaLocation", projectDir.resolve("src/main/room/schemas").absolutePath)
}

dependencies {
    implementation(projects.kpn)

    coreLibraryDesugaring(libs.desugaring)
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime)

    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.immutable)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.activity)
    implementation(libs.compose.material)
    implementation(libs.compose.tv.foundation)
    implementation(libs.compose.tv.material)

    implementation(libs.destinations)
    ksp(libs.destinations.ksp)

    implementation(libs.room)
    implementation(libs.room.ktx)
    ksp(libs.room.ksp)

    implementation(libs.coil)

    implementation(libs.exoplayer)
    implementation(libs.exoplayer.dash)
    implementation(libs.exoplayer.session)
    implementation(libs.exoplayer.ui)

    detektPlugins(libs.detekt.compose)
    detektPlugins(libs.detekt.formatting)
}
