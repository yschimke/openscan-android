package ee.schimke.openscan.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ee.schimke.openscan.components.ui.DiscoveredScannerCard
import ee.schimke.openscan.components.ui.SavedScannerCard
import ee.schimke.openscan.components.ui.SavedScannerRow
import ee.schimke.openscan.components.ui.ScannerDetailBody
import ee.schimke.openscan.components.ui.ScannerListBody
import ee.schimke.openscan.components.ui.theme.OpenScanTheme
import ee.schimke.openscan.core.discovery.DiscoveredScanner
import ee.schimke.openscan.core.manager.ScannerState
import ee.schimke.openscan.core.model.CameraInfo
import ee.schimke.openscan.core.model.DeviceInfo
import ee.schimke.openscan.core.model.Scanner
import ee.schimke.openscan.core.model.ScannerReachability
import ee.schimke.openscan.core.model.ScanSettings
import ee.schimke.openscan.core.model.ScanStatus

// The design-catalog sticker sheet (consumed by catalog.spec.json → design-artifacts). Each
// @Preview name here must match the `preview` field in catalog.spec.json.

private fun sampleScanner(host: String = "openscan.local", name: String = "Workshop OpenScan") =
  Scanner(id = Scanner.idFor(host, 80), name = name, host = host, favorite = true)

private val sampleSaved =
  listOf(
    SavedScannerRow(sampleScanner(), ScannerReachability.ONLINE),
    SavedScannerRow(sampleScanner("192.168.1.42", "Garage Mini"), ScannerReachability.OFFLINE),
  )

private val sampleDiscovered =
  listOf(
    DiscoveredScanner("OpenScan-Classic", "openscan-2.local", 80),
    DiscoveredScanner("OpenScan-Mini", "192.168.1.77", 80),
  )

private fun sampleState(active: Boolean) =
  ScannerState(
    scanner = sampleScanner(),
    reachability = ScannerReachability.ONLINE,
    device = DeviceInfo(name = "Workshop OpenScan", model = "Mini", firmwareVersion = "3.1.0"),
    cameras = listOf(CameraInfo("main", "picam2")),
    scan =
      if (active) ScanStatus(phaseRaw = "capturing", captured = 18, total = 40, project = "vase-01")
      else ScanStatus(phaseRaw = "idle"),
  )

@Preview(name = "ScannerList — populated", device = Devices.PIXEL_7, showSystemUi = true, showBackground = true)
@Composable
fun ScannerListPopulatedPreview() {
  OpenScanTheme {
    Surface {
      ScannerListBody(sampleSaved, sampleDiscovered, onOpen = {}, onAddManually = {}, onRegisterDiscovered = {})
    }
  }
}

@Preview(name = "ScannerList — empty", device = Devices.PIXEL_7, showSystemUi = true, showBackground = true)
@Composable
fun ScannerListEmptyPreview() {
  OpenScanTheme {
    Surface {
      ScannerListBody(emptyList(), emptyList(), onOpen = {}, onAddManually = {}, onRegisterDiscovered = {})
    }
  }
}

@Preview(name = "ScannerDetail — idle", device = Devices.PIXEL_7, showSystemUi = true, showBackground = true)
@Composable
fun ScannerDetailIdlePreview() {
  OpenScanTheme {
    Surface {
      ScannerDetailBody(
        state = sampleState(active = false),
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

@Preview(name = "ScannerDetail — scanning", device = Devices.PIXEL_7, showSystemUi = true, showBackground = true)
@Composable
fun ScannerDetailScanningPreview() {
  OpenScanTheme {
    Surface {
      ScannerDetailBody(
        state = sampleState(active = true),
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

@Preview(name = "SavedScannerCard", showBackground = true, widthDp = 412)
@Composable
fun SavedScannerCardPreview() {
  OpenScanTheme {
    Surface {
      SavedScannerCard(sampleScanner(), ScannerReachability.ONLINE, onClick = {}, modifier = Modifier.padding(16.dp))
    }
  }
}

@Preview(name = "DiscoveredScannerCard", showBackground = true, widthDp = 412)
@Composable
fun DiscoveredScannerCardPreview() {
  OpenScanTheme {
    Surface {
      DiscoveredScannerCard("OpenScan-Mini", "192.168.1.77:80", onAdd = {}, modifier = Modifier.padding(16.dp))
    }
  }
}
