plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.ksp) apply false
//    id("com.google.devtools.ksp").version("1.9.21-1.0.16").apply(false)

    //  kotlin("multiplatform").apply(false)
    //    id("com.android.application").apply(false)
    //    id("com.android.library").apply(false)
    //    id("org.jetbrains.compose").apply(false)
}