plugins {
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.androidKotlinMultiplatformLibrary)
}

kotlin {
  jvmToolchain(21)

  android {
    namespace = "ee.schimke.openscan.mobile"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    minSdk = libs.versions.android.minSdk.get().toInt()
  }

  sourceSets {
    commonMain.dependencies {
      api(projects.openscanCore)
      implementation(libs.kotlinx.coroutines.core)
    }
    val androidMain by getting {
      dependencies {
        // The Android integration layer: mDNS discovery via NsdManager wired to
        // the shared ScannerDiscovery interface for the host app.
        implementation(libs.kotlinx.coroutines.android)
        api(libs.androidx.core.ktx)
      }
    }
  }
}
