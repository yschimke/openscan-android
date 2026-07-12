package ee.schimke.openscan.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ee.schimke.openscan.components.ui.theme.OpenScanTheme

// Foundation sticker-sheet previews for the design catalog (catalog.spec.json). They render the
// OpenScan colour roles so the published design-artifacts bundle documents the theme.

@Composable
private fun swatch(name: String, bg: Color, fg: Color) {
  Row(
    Modifier.fillMaxWidth().background(bg).padding(12.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(name, color = fg, style = MaterialTheme.typography.labelLarge)
    Box(Modifier.size(20.dp).background(fg))
  }
}

@Composable
private fun ThemeFoundation() {
  Surface {
    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Text("OpenScan theme", style = MaterialTheme.typography.titleLarge)
      val c = MaterialTheme.colorScheme
      swatch("Primary", c.primary, c.onPrimary)
      swatch("Primary container", c.primaryContainer, c.onPrimaryContainer)
      swatch("Secondary", c.secondary, c.onSecondary)
      swatch("Tertiary", c.tertiary, c.onTertiary)
      swatch("Error", c.error, c.onError)
      swatch("Surface", c.surface, c.onSurface)
      swatch("Surface container", c.surfaceContainer, c.onSurface)
      Box(Modifier.fillMaxWidth().height(4.dp))
    }
  }
}

@Preview(name = "Theme — light", showBackground = true, widthDp = 412)
@Composable
fun ThemeFoundationOpenScanLightPreview() {
  OpenScanTheme { ThemeFoundation() }
}

@Preview(name = "Theme — dark", showBackground = true, widthDp = 412)
@Composable
fun ThemeFoundationOpenScanDarkPreview() {
  OpenScanTheme(darkTheme = true) { ThemeFoundation() }
}
