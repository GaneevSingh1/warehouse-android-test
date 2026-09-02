import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ktlint)
}

val generateApiConfig by tasks.registering {
    val outputDir = layout.buildDirectory.dir("generated/apiConfig/src/commonMain/kotlin")
    outputs.dir(outputDir)

    val subscriptionKey = providers.gradleProperty("OCP_APIM_SUBSCRIPTION_KEY")
        .orElse(providers.environmentVariable("OCP_APIM_SUBSCRIPTION_KEY"))
        .orElse("")
    inputs.property("subscriptionKey", subscriptionKey)
    outputs.upToDateWhen { subscriptionKey.get().trim().isNotEmpty() }

    doLast {
        val key = subscriptionKey.get().trim()
        if (key.isEmpty()) {
            throw GradleException(
                "OCP_APIM_SUBSCRIPTION_KEY is required. Set it as a Gradle property " +
                    "(-POCP_APIM_SUBSCRIPTION_KEY=...) or environment variable.",
            )
        }
        val file = outputDir.get()
            .file("nz/co/warehouseandroidtest/data/remote/GeneratedApiConfig.kt")
            .asFile
        file.parentFile.mkdirs()
        val escapedKey = key
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\$", "\\\$")
        file.writeText(
            buildString {
                appendLine("package nz.co.warehouseandroidtest.data.remote")
                appendLine()
                appendLine("internal object GeneratedApiConfig {")
                appendLine("    const val SUBSCRIPTION_KEY: String = \"$escapedKey\"")
                appendLine("}")
            },
        )
    }
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    android {
        namespace = "nz.co.warehouseandroidtest.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
        androidResources {
            enable = true
        }
        withHostTest {
            isIncludeAndroidResources = true
        }
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.uiTooling)
            implementation(libs.ktor.client.android)
            implementation(libs.koin.android)
        }
        commonMain {
            kotlin.srcDir(generateApiConfig)
            dependencies {
                implementation(libs.compose.runtime)
                implementation(libs.compose.foundation)
                implementation(libs.compose.material3)
                implementation(libs.compose.ui)
                implementation(libs.compose.components.resources)
                implementation(libs.compose.uiToolingPreview)
                implementation(libs.androidx.lifecycle.viewmodelCompose)
                implementation(libs.androidx.lifecycle.runtimeCompose)
                implementation(libs.kotlinx.coroutines)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.androidx.datastore.preferences)
                implementation(libs.koin.core)
                implementation(libs.koin.compose)
                implementation(libs.koin.compose.viewmodel)
            }
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.ktor.client.mock)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}

tasks.matching { it.name.startsWith("runKtlint") }.configureEach {
    dependsOn(generateApiConfig)
}
