package ee.schimke.openscan.app.di

import android.content.Context
import androidx.compose.runtime.staticCompositionLocalOf
import ee.schimke.openscan.app.data.DataStoreScannerStore
import ee.schimke.openscan.core.api.OpenScanApi
import ee.schimke.openscan.core.api.OpenScanHttpApi
import ee.schimke.openscan.core.api.openScanHttpClient
import ee.schimke.openscan.core.discovery.ScannerDiscovery
import ee.schimke.openscan.core.manager.OpenScanManager
import ee.schimke.openscan.core.model.Scanner
import ee.schimke.openscan.core.repository.ScannerRepository
import ee.schimke.openscan.mobile.NsdScannerDiscovery
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

/**
 * Single, explicit application object graph. Every app-scoped singleton is constructed here and
 * handed out via properties. A plain class so dependencies are obvious, ordering is explicit, and
 * collaborators can be built in tests without an [android.app.Application].
 */
class AppGraph(appContext: Context) {

  /** App-lifetime coroutine scope; cancelled on [close]. */
  val applicationScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

  private val httpClient: HttpClient = openScanHttpClient()

  val discovery: ScannerDiscovery = NsdScannerDiscovery(appContext)

  val repository: ScannerRepository = ScannerRepository(DataStoreScannerStore(appContext))

  /** Build a manager bound to one device; the detail screen owns its polling loop. */
  fun managerFor(scanner: Scanner): OpenScanManager = OpenScanManager(apiFor(scanner), scanner)

  fun apiFor(scanner: Scanner): OpenScanApi = OpenScanHttpApi(httpClient, scanner.baseUrl)

  /** Tear down app-scoped coroutines and the shared HTTP client. */
  fun close() {
    discovery.stop()
    httpClient.close()
    applicationScope.cancel()
  }
}

/** Implemented by the [android.app.Application] so any [Context] can reach the graph. */
interface AppGraphHolder {
  val appGraph: AppGraph
}

/** Resolve the [AppGraph] from any [Context] without a global singleton. */
fun Context.appGraph(): AppGraph = (applicationContext as AppGraphHolder).appGraph

/**
 * CompositionLocal carrying the [AppGraph] to composables, provided once at the activity root. Keeps
 * screens from reaching into the Application.
 */
val LocalAppGraph =
  staticCompositionLocalOf<AppGraph> { error("AppGraph not provided. Wrap content in OpenScanApp.") }
