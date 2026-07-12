package ee.schimke.openscan.components.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ee.schimke.openscan.components.ui.theme.Dimens
import ee.schimke.openscan.core.model.Scanner
import ee.schimke.openscan.core.model.ScannerReachability

/** A small coloured dot + label reflecting a scanner's reachability. */
@Composable
fun ReachabilityPill(reachability: ScannerReachability, modifier: Modifier = Modifier) {
  val (color, label) =
    when (reachability) {
      ScannerReachability.ONLINE -> MaterialTheme.colorScheme.primary to "Online"
      ScannerReachability.OFFLINE -> MaterialTheme.colorScheme.error to "Offline"
      ScannerReachability.UNKNOWN -> MaterialTheme.colorScheme.outline to "Checking…"
    }
  Row(
    modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(Dimens.XS),
  ) {
    Box(Modifier.size(10.dp).background(color, CircleShape))
    Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
  }
}

/** A saved scanner as a tappable card with name, host and reachability. */
@Composable
fun SavedScannerCard(
  scanner: Scanner,
  reachability: ScannerReachability,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Card(
    onClick = onClick,
    modifier = modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
  ) {
    Row(
      Modifier.fillMaxWidth().padding(Dimens.L),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(Dimens.XXS)) {
        Text(scanner.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Text(
          scanner.baseUrl.removePrefix("http://"),
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
      ReachabilityPill(reachability)
    }
  }
}

/** A device found by mDNS that the user can add with one tap. */
@Composable
fun DiscoveredScannerCard(
  name: String,
  host: String,
  onAdd: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Card(
    onClick = onAdd,
    modifier = modifier.fillMaxWidth(),
    colors =
      CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
  ) {
    Row(
      Modifier.fillMaxWidth().padding(Dimens.L),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(Dimens.XXS)) {
        Text(name, style = MaterialTheme.typography.titleMedium)
        Text(host, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
      }
      Text("Add", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
    }
  }
}

/** A section header used across the screens. */
@Composable
fun SectionHeader(text: String, modifier: Modifier = Modifier) {
  Text(
    text,
    modifier = modifier.padding(top = Dimens.S, bottom = Dimens.XS),
    style = MaterialTheme.typography.titleSmall,
    color = MaterialTheme.colorScheme.primary,
    fontWeight = FontWeight.SemiBold,
  )
}
