package ee.schimke.openscan.core

import ee.schimke.openscan.core.api.OpenScanEndpoints
import ee.schimke.openscan.core.api.OpenScanException
import ee.schimke.openscan.core.api.OpenScanHttpApi
import ee.schimke.openscan.core.model.ScanPhase
import ee.schimke.openscan.core.model.ScanSettings
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.http.HttpHeaders
import io.ktor.http.ContentType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class OpenScanHttpApiTest {

  private fun api(handler: suspend (String) -> Pair<HttpStatusCode, String>): OpenScanHttpApi {
    val engine = MockEngine { request ->
      val (status, body) = handler(request.url.encodedPath)
      respond(
        content = body,
        status = status,
        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
      )
    }
    return OpenScanHttpApi(HttpClient(engine), "http://openscan.local")
  }

  @Test
  fun device_hitsVersionedPath_andDecodes() = runTest {
    var seen = ""
    val api = api { path ->
      seen = path
      HttpStatusCode.OK to """{"name":"My OpenScan","model":"mini","firmware_version":"3.1.0"}"""
    }
    val device = api.device()
    assertEquals(OpenScanEndpoints.DEVICE, seen)
    assertEquals("My OpenScan", device.name)
    assertEquals("3.1.0", device.firmwareVersion)
  }

  @Test
  fun cameras_decodesList_ignoringUnknownKeys() = runTest {
    val api = api { HttpStatusCode.OK to """[{"name":"main","type":"picam","extra":true}]""" }
    val cams = api.cameras()
    assertEquals(1, cams.size)
    assertEquals("main", cams.first().name)
  }

  @Test
  fun startScan_postsSettings_andReturnsStatus() = runTest {
    var seen = ""
    val api = api { path ->
      seen = path
      HttpStatusCode.OK to """{"phase":"initializing","captured":0,"total":40}"""
    }
    val status = api.startScan(ScanSettings(points = 40))
    assertEquals(OpenScanEndpoints.SCAN_START, seen)
    assertEquals(ScanPhase.INITIALIZING, status.phase)
    assertEquals(40, status.total)
  }

  @Test
  fun httpError_becomesOpenScanException() = runTest {
    val api = api { HttpStatusCode.InternalServerError to "boom" }
    val e = assertFailsWith<OpenScanException> { api.device() }
    assertTrue(e.message!!.contains("device failed"))
  }
}
