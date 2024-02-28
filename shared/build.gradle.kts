import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.ksp)

    kotlin("plugin.serialization") version "1.9.21"
}

kotlin {
    androidTarget()

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

            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")

            dependencies {

                //KOIN
                implementation(project.dependencies.platform(libs.koin.bom))
                implementation(libs.koin.core)
                implementation(libs.koin.compose)

                //KOIN ANNOTATIONS
                implementation(libs.koin.annotations)


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
                api(compose.preview)
                api(compose.uiTooling)
                implementation("androidx.camera:camera-core:1.3.1")
                implementation("androidx.camera:camera-camera2:1.3.1")
                implementation("androidx.camera:camera-lifecycle:1.3.1")

                implementation("androidx.camera:camera-view:1.3.1")
                implementation("androidx.camera:camera-extensions:1.3.1")
                implementation("com.google.accompanist:accompanist-permissions:0.34.0")
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

dependencies {
    add("kspCommonMainMetadata", libs.koin.ksp.compiler)
}

// Ensure all Kotlin compile tasks depend on kspCommonMainKotlinMetadata for prior code processing.
tasks.withType<KotlinCompile<*>>().configureEach {
    if (name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}

// Make all SourcesJar tasks depend on kspCommonMainKotlinMetadata.
afterEvaluate {
    tasks.filter { task: Task ->
        task.name.contains("SourcesJar", true)
    }.forEach {
        it.dependsOn("kspCommonMainKotlinMetadata")
    }
}

android {
    namespace = "org.taske.sicktrainer.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
}
