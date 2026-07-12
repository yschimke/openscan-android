package ee.schimke.openscan.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import ee.schimke.openscan.components.ui.theme.OpenScanDarkColors
import ee.schimke.openscan.components.ui.theme.OpenScanLightColors
import ee.schimke.openscan.components.ui.theme.OpenScanShapes

/**
 * Android app theme: the shared OpenScan palette, upgraded to Material You dynamic color on Android
 * 12+ when available. The shared composables use the branded palette on the desktop parity render;
 * here users get their wallpaper colours if the platform supports it.
 */
@Composable
fun OpenScanAppTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = true,
  content: @Composable () -> Unit,
) {
  val context = LocalContext.current
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      darkTheme -> OpenScanDarkColors
      else -> OpenScanLightColors
    }
  MaterialTheme(colorScheme = colorScheme, shapes = OpenScanShapes, content = content)
}
