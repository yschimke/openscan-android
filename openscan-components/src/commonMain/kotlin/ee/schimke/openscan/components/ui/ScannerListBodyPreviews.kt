package ee.schimke.openscan.components.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import ee.schimke.openscan.components.ui.theme.OpenScanTheme

// Design-parity preview subjects for the scanner list / home screen, rendered on the CMP desktop
// path. Checked against design/ScannerList.{light,dark}.html; the rendered reference | candidate |
// diff is published to the design-parity/main branch. See docs/design-parity.md.

@Preview(showBackground = true, showSystemUi = true, device = Devices.PIXEL_7, name = "Scanner list")
@Composable
fun ScannerListBodyPreview() {
  OpenScanTheme {
    Surface {
      ScannerListBody(
        saved = PreviewData.savedRows,
        discovered = PreviewData.discovered,
        onOpen = {},
        onAddManually = {},
        onRegisterDiscovered = {},
      )
    }
  }
}

@Preview(
  showBackground = true,
  showSystemUi = true,
  device = Devices.PIXEL_7,
  uiMode = 0x20, // Configuration.UI_MODE_NIGHT_YES
  name = "Scanner list — dark",
)
@Composable
fun ScannerListBodyDarkPreview() {
  OpenScanTheme(darkTheme = true) {
    Surface {
      ScannerListBody(
        saved = PreviewData.savedRows,
        discovered = PreviewData.discovered,
        onOpen = {},
        onAddManually = {},
        onRegisterDiscovered = {},
      )
    }
  }
}
