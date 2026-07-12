import com.github.triplet.gradle.androidpublisher.ReleaseStatus

val appVersionName = "0.1.0" // x-release-please-version

// Pack MAJOR.MINOR.PATCH into a monotonic int. Caps at major < 22.
val appVersionCode: Int =
  run {
      val parts = appVersionName.split(".", "-").mapNotNull { it.toIntOrNull() }
      val major = parts.getOrNull(0) ?: 0
      val minor = parts.getOrNull(1) ?: 0
      val patch = parts.getOrNull(2) ?: 0
      major * 10_000 + minor * 100 + patch
    }
    .coerceAtLeast(1)

plugins {
  // AGP 9 has built-in Kotlin support, so `com.android.application` alone covers
  // both Android and Kotlin compilation.
  alias(libs.plugins.androidApplication)
  alias(libs.plugins.composeMultiplatform)
  alias(libs.plugins.composeCompiler)
  alias(libs.plugins.kotlinSerialization)
  alias(libs.plugins.playPublisher)
}

play {
  track.set("internal")
  defaultToAppBundles.set(true)
  releaseStatus.set(ReleaseStatus.DRAFT)
  // Skip API calls in CI runs that build but don't publish (e.g. PRs).
  enabled.set(System.getenv("ANDROID_PUBLISHER_CREDENTIALS") != null)
}

kotlin { jvmToolchain(21) }

android {
  namespace = "ee.schimke.openscan.app"
  compileSdk = libs.versions.android.compileSdk.get().toInt()
  defaultConfig {
    applicationId = "ee.schimke.openscan"
    minSdk = libs.versions.android.minSdk.get().toInt()
    targetSdk = libs.versions.android.targetSdk.get().toInt()
    versionCode = appVersionCode
    versionName = appVersionName
  }
  val releaseKeystorePath = System.getenv("OPENSCAN_KEYSTORE_PATH")
  signingConfigs {
    if (releaseKeystorePath != null) {
      create("release") {
        storeFile = file(releaseKeystorePath)
        storePassword = System.getenv("OPENSCAN_KEYSTORE_PASSWORD")
        keyAlias = System.getenv("OPENSCAN_KEY_ALIAS")
        keyPassword = System.getenv("OPENSCAN_KEY_PASSWORD")
      }
    }
  }
  buildTypes {
    getByName("release") {
      isMinifyEnabled = false
      if (releaseKeystorePath != null) {
        signingConfig = signingConfigs.getByName("release")
      }
    }
  }
  testOptions.unitTests { isIncludeAndroidResources = true }
}

dependencies {
  implementation(projects.openscanCore)
  implementation(projects.openscanComponents)
  implementation(projects.openscanMobile)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.androidx.activity.compose)
  implementation(libs.compose.runtime)
  implementation(libs.compose.foundation)
  implementation(libs.compose.material3)
  implementation(libs.compose.material.icons.extended)
  implementation(libs.compose.ui)
  implementation(libs.compose.uiToolingPreview)
  implementation(libs.androidx.lifecycle.runtimeCompose)
  implementation(libs.androidx.lifecycle.viewmodelCompose)
  implementation(libs.androidx.navigation3.runtime)
  implementation(libs.androidx.navigation3.ui)
  implementation(libs.androidx.datastore)
  debugImplementation(libs.compose.uiTooling)
  testImplementation(libs.kotlin.test)
  testImplementation(libs.robolectric)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.testExt.junit)
}
