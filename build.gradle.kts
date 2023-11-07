plugins {
    kotlin("android") version libs.versions.kotlin apply false
    kotlin("plugin.serialization") version libs.versions.kotlin apply false
    id("com.android.application") version libs.versions.agp apply false
    id("com.google.devtools.ksp") version libs.versions.ksp apply false
}