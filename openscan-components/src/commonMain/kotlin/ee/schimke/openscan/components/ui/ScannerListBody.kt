package ee.schimke.openscan.components.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import ee.schimke.openscan.components.ui.theme.Dimens
import ee.schimke.openscan.core.discovery.DiscoveredScanner
import ee.schimke.openscan.core.model.Scanner
import ee.schimke.openscan.core.model.ScannerReachability

/** A saved scanner paired with its last-known reachability, for the list. */
data class SavedScannerRow(val scanner: Scanner, val reachability: ScannerReachability)

/**
 * Stateless body of the scanner list / home screen: the user's saved OpenScan devices plus any
 * found on the network by mDNS. Presentational only (no ViewModel, no side effects) so it renders on
 * the CMP desktop path for design-parity; the app wraps it with live data and navigation.
 */
@Composable
fun ScannerListBody(
  saved: List<SavedScannerRow>,
  discovered: List<DiscoveredScanner>,
  onOpen: (Scanner) -> Unit,
  onAddManually: () -> Unit,
  onRegisterDiscovered: (DiscoveredScanner) -> Unit,
  modifier: Modifier = Modifier,
) {
  LazyColumn(
    modifier = modifier.fillMaxSize().padding(horizontal = Dimens.ScreenPadding),
    verticalArrangement = Arrangement.spacedBy(Dimens.CardGap),
    contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = Dimens.L),
  ) {
    if (saved.isEmpty() && discovered.isEmpty()) {
      item { EmptyState(onAddManually) }
      return@LazyColumn
    }

    if (saved.isNotEmpty()) {
      item { SectionHeader("Saved scanners") }
      items(saved, key = { it.scanner.id.value }) { row ->
        SavedScannerCard(row.scanner, row.reachability, onClick = { onOpen(row.scanner) })
      }
    }

    val savedHosts = saved.mapTo(mutableSetOf()) { it.scanner.host }
    val fresh = discovered.filter { it.host !in savedHosts }
    if (fresh.isNotEmpty()) {
      item { SectionHeader("On your network") }
      items(fresh, key = { it.key }) { d ->
        DiscoveredScannerCard(d.name, "${d.host}:${d.port}", onAdd = { onRegisterDiscovered(d) })
      }
    }

    item {
      OutlinedButton(onClick = onAddManually, modifier = Modifier.fillMaxWidth().padding(top = Dimens.S)) {
        Text("Add a scanner by address")
      }
    }
  }
}

@Composable
private fun EmptyState(onAddManually: () -> Unit) {
  Column(
    Modifier.fillMaxWidth().padding(vertical = Dimens.XXXL),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(Dimens.M),
  ) {
    Text("No scanners yet", style = MaterialTheme.typography.titleLarge)
    Text(
      "OpenScan devices on your Wi-Fi will appear here automatically. You can also add one by its address (e.g. openscan.local).",
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center,
    )
    OutlinedButton(onClick = onAddManually) { Text("Add a scanner") }
  }
}
