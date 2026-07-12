package ee.schimke.openscan.core

import ee.schimke.openscan.core.model.ScanPhase
import ee.schimke.openscan.core.model.ScanStatus
import ee.schimke.openscan.core.model.Scanner
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ScannerModelTest {

  @Test
  fun baseUrl_omitsDefaultPort() {
    assertEquals("http://openscan.local", Scanner(Scanner.idFor("openscan.local", 80), "n", "openscan.local").baseUrl)
    assertEquals(
      "http://192.168.1.5:8000",
      Scanner(Scanner.idFor("192.168.1.5", 8000), "n", "192.168.1.5", 8000).baseUrl,
    )
  }

  @Test
  fun id_isCaseInsensitiveAndPortAware() {
    assertEquals(Scanner.idFor("OpenScan.Local", 80), Scanner.idFor("openscan.local", 80))
    assertTrue(Scanner.idFor("host", 8000) != Scanner.idFor("host", 80))
  }

  @Test
  fun scanStatus_mapsPhaseAndFraction() {
    val s = ScanStatus(phaseRaw = "capturing", captured = 20, total = 40)
    assertEquals(ScanPhase.CAPTURING, s.phase)
    assertEquals(0.5f, s.fraction)
    assertTrue(s.isActive)
  }

  @Test
  fun scanStatus_idleIsNotActive_andClampsFraction() {
    assertEquals(0f, ScanStatus(total = 0, captured = 5).fraction)
    assertTrue(!ScanStatus(phaseRaw = "idle").isActive)
    assertEquals(1f, ScanStatus(phaseRaw = "done", captured = 99, total = 40).fraction)
  }
}
