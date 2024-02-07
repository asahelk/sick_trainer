import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
}

kotlin {
//    androidTarget {
//        compilations.all {
//            kotlinOptions {
//                jvmTarget = "1.8"
//            }
//        }
//    }
    androidTarget()

    sourceSets {
        val androidMain by getting {
            dependencies {
                implementation(project(":shared"))
            }
        }
//        androidMain.dependencies {
//            implementation(libs.compose.ui.tooling.preview)
//            implementation(libs.androidx.activity.compose)
//        }
//        commonMain.dependencies {
//            implementation(compose.runtime)
//            implementation(compose.foundation)
//            implementation(compose.material)
//            implementation(compose.ui)
//            @OptIn(ExperimentalComposeLibrary::class)
//            implementation(compose.components.resources)
//            implementation(projects.shared)
//        }
    }
}

android {
    namespace = "org.taske.sicktrainer"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "org.taske.sicktrainer"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    dependencies {
        debugImplementation(libs.compose.ui.tooling)
    }
    kotlin {
        jvmToolchain(17)
    }
}

