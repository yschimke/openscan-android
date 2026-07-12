package ee.schimke.openscan.core.repository

import ee.schimke.openscan.core.model.Scanner
import ee.schimke.openscan.core.model.ScannerId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * The app's view over the user's registered scanners: register/rename/forget and observe. Thin by
 * design — it owns the domain rules (id derivation, favourite handling) while [ScannerStore] owns
 * persistence.
 */
class ScannerRepository(private val store: ScannerStore) {

  val scanners: Flow<List<Scanner>> = store.scanners

  fun scanner(id: ScannerId): Flow<Scanner?> = store.scanners.map { list -> list.firstOrNull { it.id == id } }

  /**
   * Register (or update) a scanner from a user-entered host. Marks it a favourite so it survives the
   * discovery list churn. Returns the resulting [Scanner].
   */
  suspend fun register(host: String, port: Int = 80, name: String? = null): Scanner {
    val cleanHost = host.trim().removePrefix("http://").removePrefix("https://").substringBefore("/")
    val id = Scanner.idFor(cleanHost, port)
    val scanner =
      Scanner(id = id, name = name?.ifBlank { null } ?: cleanHost, host = cleanHost, port = port, favorite = true)
    store.upsert(scanner)
    return scanner
  }

  suspend fun rename(id: ScannerId, name: String, current: List<Scanner>) {
    current.firstOrNull { it.id == id }?.let { store.upsert(it.copy(name = name)) }
  }

  suspend fun forget(id: ScannerId) = store.remove(id.value)
}
