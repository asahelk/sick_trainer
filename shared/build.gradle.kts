import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsCompose)

    kotlin("plugin.serialization") version "1.9.21"
}

kotlin {
    androidTarget()
//    androidTarget {
//        compilations.all {
//            kotlinOptions {
//                jvmTarget = "1.8"
//            }
//        }
//    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.material3)
                implementation(compose.ui)
                @OptIn(ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)

                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)

                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)

                implementation(libs.mvvm.core)
                api(libs.mvvm.compose)

                api(libs.image.loader)
            }
        }
        val androidMain by getting {
            dependencies {
                api(libs.androidx.activity.compose)
                implementation(libs.compose.ui.tooling.preview)
                implementation(libs.ktor.client.android)

//                api(libs.androidx.activity.compose)
//                api("androidx.appcompat:appcompat:1.6.1")
//                api("androidx.core:core-ktx:1.10.1")
//                implementation ("androidx.compose.ui:ui:1.5.4")
//                implementation(libs.compose.ui.tooling.preview)
//                implementation(libs.ktor.client.android)
//                implementation("io.ktor:ktor-client-okhttp:2.3.5")
            }
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }
    }
}

android {
    namespace = "org.taske.sicktrainer.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }

}
