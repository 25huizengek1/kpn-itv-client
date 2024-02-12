import io.gitlab.arturbosch.detekt.Detekt

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.lint) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.detekt)
}

allprojects {
    group = "me.huizengek.kpninteractievetv"
    version = "1.0.0"

    repositories {
        google()
        mavenCentral()
    }

    apply(plugin = "io.gitlab.arturbosch.detekt")

    detekt {
        buildUponDefaultConfig = true
        allRules = false
        config.setFrom("$rootDir/detekt.yml")
    }

    tasks.withType<Detekt>().configureEach {
        jvmTarget = "11"
        reports {
            html.required = true
        }
    }
}
