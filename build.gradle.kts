plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.ksp) apply false
}

allprojects {
    group = "me.huizengek.kpninteractievetv"
    version = "1.0.0"

    repositories {
        google()
        mavenCentral()
    }
}