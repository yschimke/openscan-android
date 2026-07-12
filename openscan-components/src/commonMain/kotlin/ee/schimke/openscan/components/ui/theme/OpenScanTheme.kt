package ee.schimke.openscan.components.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

/**
 * Multiplatform OpenScan theme for the shared composables (and the design-parity desktop render).
 * Pure: the OpenScan branded palette and shapes, with no platform chrome. The app's own theme wraps
 * this with Android-only dynamic color + edge-to-edge handling.
 */
@Composable
fun OpenScanTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
  MaterialTheme(
    colorScheme = if (darkTheme) OpenScanDarkColors else OpenScanLightColors,
    shapes = OpenScanShapes,
    content = content,
  )
}
