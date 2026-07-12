package ee.schimke.openscan.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import ee.schimke.openscan.app.di.LocalAppGraph
import ee.schimke.openscan.app.di.appGraph
import ee.schimke.openscan.app.ui.AddScannerScreen
import ee.schimke.openscan.app.ui.ScannerDetailScreen
import ee.schimke.openscan.app.ui.ScannerListScreen
import ee.schimke.openscan.app.ui.WebFallbackScreen
import ee.schimke.openscan.app.ui.theme.OpenScanAppTheme
import kotlinx.serialization.Serializable

// Type-safe destination keys for androidx.navigation3. Each NavKey is @Serializable so the back
// stack survives process death via rememberNavBackStack's saveable bundling.
@Serializable private data object ScannerListRoute : NavKey

@Serializable private data object AddScannerRoute : NavKey

@Serializable private data class DetailRoute(val scannerId: String) : NavKey

@Serializable private data class WebRoute(val url: String, val title: String) : NavKey

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    val graph = appGraph()
    setContent { CompositionLocalProvider(LocalAppGraph provides graph) { OpenScanAppUi() } }
  }
}

@Composable
private fun OpenScanAppUi() {
  OpenScanAppTheme {
    Surface {
      val backStack = rememberNavBackStack(ScannerListRoute)
      NavDisplay(
        backStack = backStack,
        onBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
        entryProvider =
          entryProvider {
            entry<ScannerListRoute> {
              ScannerListScreen(
                onOpenScanner = { scanner -> backStack.add(DetailRoute(scanner.id.value)) },
                onAddManually = { backStack.add(AddScannerRoute) },
              )
            }
            entry<AddScannerRoute> {
              AddScannerScreen(
                onAdded = { scanner ->
                  backStack.removeLastOrNull()
                  backStack.add(DetailRoute(scanner.id.value))
                },
                onBack = { backStack.removeLastOrNull() },
              )
            }
            entry<DetailRoute> { route ->
              ScannerDetailScreen(
                scannerId = route.scannerId,
                onBack = { backStack.removeLastOrNull() },
                onOpenWeb = { url, title -> backStack.add(WebRoute(url, title)) },
              )
            }
            entry<WebRoute> { route ->
              WebFallbackScreen(
                url = route.url,
                title = route.title,
                onBack = { backStack.removeLastOrNull() },
              )
            }
          },
      )
    }
  }
}
