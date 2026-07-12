package ee.schimke.openscan.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ee.schimke.openscan.app.di.LocalAppGraph
import ee.schimke.openscan.components.ui.ScannerListBody
import ee.schimke.openscan.components.ui.SavedScannerRow
import ee.schimke.openscan.core.model.Scanner
import ee.schimke.openscan.core.model.ScannerReachability
import kotlinx.coroutines.launch

/**
 * Home screen: the user's saved scanners plus devices discovered on the local network. Owns
 * discovery lifecycle and turns a tapped discovered device into a registered one.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerListScreen(onOpenScanner: (Scanner) -> Unit, onAddManually: () -> Unit) {
  val graph = LocalAppGraph.current
  val scope = rememberCoroutineScope()

  val saved by graph.repository.scanners.collectAsStateWithLifecycle(initialValue = emptyList())
  val discovery = graph.discovery
  val discovered by discovery.discovered.collectAsStateWithLifecycle(initialValue = emptyList())

  androidx.compose.runtime.DisposableEffect(discovery) {
    discovery.start()
    onDispose { discovery.stop() }
  }

  val rows = remember(saved) { saved.map { SavedScannerRow(it, ScannerReachability.UNKNOWN) } }

  Scaffold(topBar = { TopAppBar(title = { Text("OpenScan") }) }) { padding ->
    ScannerListBody(
      saved = rows,
      discovered = discovered,
      onOpen = onOpenScanner,
      onAddManually = onAddManually,
      onRegisterDiscovered = { d ->
        scope.launch {
          val scanner = graph.repository.register(host = d.host, port = d.port, name = d.name)
          onOpenScanner(scanner)
        }
      },
      modifier = Modifier.padding(padding),
    )
  }
}
