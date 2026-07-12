package ee.schimke.openscan.core.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout

/**
 * Builds the single shared [HttpClient] the app uses to talk to every device. CIO is used because
 * it compiles for both the JVM (desktop/tests) and Android targets, keeping the whole HTTP layer in
 * commonMain.
 *
 * Timeouts are deliberately short: devices are on the local network, and a slow endpoint should
 * fall back to the web view quickly rather than hang a screen.
 */
fun openScanHttpClient(requestTimeoutMs: Long = 8_000): HttpClient =
  HttpClient(CIO) {
    expectSuccess = false
    install(HttpTimeout) {
      requestTimeoutMillis = requestTimeoutMs
      connectTimeoutMillis = 4_000
      socketTimeoutMillis = requestTimeoutMs
    }
  }
