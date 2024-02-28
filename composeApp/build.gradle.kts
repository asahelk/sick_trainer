import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
//    kotlin("multiplatform")
//    id("com.android.application")
//    id("org.jetbrains.compose")
//    id("com.google.devtools.ksp")

    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.ksp)
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
                implementation(libs.compose.ui.tooling.preview)
                api(libs.androidx.activity.compose)
                implementation("androidx.camera:camera-camera2:1.3.1")
                implementation("androidx.camera:camera-lifecycle:1.3.1")
                implementation("androidx.camera:camera-view:1.3.1")
                implementation("androidx.camera:camera-extensions:1.3.1")
            }
        }

        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.material3)
                implementation(compose.ui)
                @OptIn(ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
                implementation(projects.shared)

            }
        }

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
        //Koin
        implementation(project.dependencies.platform(libs.koin.bom))
        implementation(libs.koin.core)
        implementation(libs.koin.android)
        implementation(libs.koin.androidx.compose)

        //Koin Annotations
        implementation(libs.koin.annotations)
        add("kspAndroid",libs.koin.ksp.compiler)
//        ksp(libs.koin.ksp.compiler)

        debugImplementation(libs.compose.ui.tooling)
    }
    kotlin {
        jvmToolchain(17)
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
}

