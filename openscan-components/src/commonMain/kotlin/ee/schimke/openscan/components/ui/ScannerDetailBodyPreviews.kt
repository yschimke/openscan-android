package ee.schimke.openscan.components.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import ee.schimke.openscan.components.ui.theme.OpenScanTheme
import ee.schimke.openscan.core.model.ScanSettings

// Design-parity preview subjects for the scanner detail screen (idle + scanning), rendered on the
// CMP desktop path. Checked against design/ScannerDetail.{light,dark}.html. See
// docs/design-parity.md.

@Preview(showBackground = true, showSystemUi = true, device = Devices.PIXEL_7, name = "Scanner detail")
@Composable
fun ScannerDetailBodyPreview() {
  OpenScanTheme {
    Surface {
      ScannerDetailBody(
        state = PreviewData.onlineState(active = false),
        settings = ScanSettings(points = 40, rotations = 2),
        onSettingsChange = {},
        onStartScan = {},
        onStopScan = {},
        onRotate = {},
        onOpenWeb = {},
      )
    }
  }
}

@Preview(
  showBackground = true,
  showSystemUi = true,
  device = Devices.PIXEL_7,
  uiMode = 0x20,
  name = "Scanner detail — dark",
)
@Composable
fun ScannerDetailBodyDarkPreview() {
  OpenScanTheme(darkTheme = true) {
    Surface {
      ScannerDetailBody(
        state = PreviewData.onlineState(active = true),
        settings = ScanSettings(points = 40, rotations = 2),
        onSettingsChange = {},
        onStartScan = {},
        onStopScan = {},
        onRotate = {},
        onOpenWeb = {},
      )
    }
  }
}
