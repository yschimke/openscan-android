package ee.schimke.openscan.components.ui

import ee.schimke.openscan.core.discovery.DiscoveredScanner
import ee.schimke.openscan.core.manager.ScannerState
import ee.schimke.openscan.core.model.CameraInfo
import ee.schimke.openscan.core.model.DeviceInfo
import ee.schimke.openscan.core.model.Scanner
import ee.schimke.openscan.core.model.ScannerReachability
import ee.schimke.openscan.core.model.ScanStatus

/** Shared sample data for the design-parity previews (and app @Preview reuse). */
internal object PreviewData {
  fun scanner(host: String = "openscan.local", name: String = "Workshop OpenScan") =
    Scanner(id = Scanner.idFor(host, 80), name = name, host = host, favorite = true)

  val savedRows =
    listOf(
      SavedScannerRow(scanner(), ScannerReachability.ONLINE),
      SavedScannerRow(
        scanner("192.168.1.42", "Garage Mini").copy(port = 8000),
        ScannerReachability.OFFLINE,
      ),
    )

  val discovered =
    listOf(
      DiscoveredScanner("OpenScan-Classic", "openscan-2.local", 80),
      DiscoveredScanner("OpenScan-Mini", "192.168.1.77", 80),
    )

  fun onlineState(active: Boolean = false) =
    ScannerState(
      scanner = scanner(),
      reachability = ScannerReachability.ONLINE,
      device = DeviceInfo(name = "Workshop OpenScan", model = "Mini", firmwareVersion = "3.1.0"),
      cameras = listOf(CameraInfo("main", "picam2")),
      scan =
        if (active) ScanStatus(phaseRaw = "capturing", captured = 18, total = 40, project = "vase-01")
        else ScanStatus(phaseRaw = "idle"),
    )
}
