plugins {
  // Unified AGP-9 plugin replacing com.android.library + kotlinMultiplatform.
  // Android target is configured via `kotlin { android { ... } }` below.
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.androidKotlinMultiplatformLibrary)
  alias(libs.plugins.kotlinSerialization)
}

kotlin {
  jvmToolchain(21)

  android {
    namespace = "ee.schimke.openscan.core"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    minSdk = libs.versions.android.minSdk.get().toInt()
  }
  jvm()

  sourceSets {
    commonMain.dependencies {
      implementation(libs.kotlinx.coroutines.core)
      api(libs.kotlinx.serialization.json)
      api(libs.kotlinx.datetime)
      // Ktor CIO client works on both JVM and Android, so the whole HTTP layer
      // lives in commonMain — no expect/actual engine plumbing needed.
      api(libs.ktor.client.core)
      implementation(libs.ktor.client.cio)
      implementation(libs.ktor.client.content.negotiation)
      implementation(libs.ktor.serialization.kotlinx.json)
    }
    commonTest.dependencies {
      implementation(libs.kotlin.test)
      implementation(libs.kotlinx.coroutines.test)
      implementation(libs.ktor.client.mock)
    }
  }
}
