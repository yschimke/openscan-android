plugins {
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.androidKotlinMultiplatformLibrary)
  alias(libs.plugins.composeMultiplatform)
  alias(libs.plugins.composeCompiler)
  // Exposes this module's desktop previews (ScannerList/ScannerDetail bodies) to
  // the compose-preview renderer + design-parity. Must be applied explicitly:
  // the plugin skips com.android.kotlin.multiplatform.library modules by default.
  alias(libs.plugins.composePreview)
}

kotlin {
  jvmToolchain(21)

  android {
    namespace = "ee.schimke.openscan.components"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    minSdk = libs.versions.android.minSdk.get().toInt()
  }

  // Desktop (JVM) target so the shared presentational composables render on the
  // cheaper CMP/Skiko path for design-parity. Android-only deps stay in androidMain.
  jvm("desktop")

  sourceSets {
    commonMain.dependencies {
      api(projects.openscanCore)
      implementation(libs.kotlinx.coroutines.core)
      api(libs.compose.runtime)
      api(libs.compose.foundation)
      api(libs.compose.material3)
      api(libs.compose.ui)
      api(libs.compose.uiToolingPreview)
      // Theme catalogs are declared in shared code, so the annotations must be
      // reachable from commonMain. Reflection-only markers, no runtime footprint.
      implementation(libs.compose.preview.annotations)
    }
    val androidMain by getting {
      dependencies {
        implementation(libs.kotlinx.coroutines.android)
        api(libs.compose.material.icons.extended)
        api(libs.androidx.activity.compose)
        api(libs.androidx.core.ktx)
      }
    }
  }
}
