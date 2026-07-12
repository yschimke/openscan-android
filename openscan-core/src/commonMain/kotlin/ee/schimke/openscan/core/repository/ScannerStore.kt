package ee.schimke.openscan.core.repository

import ee.schimke.openscan.core.model.Scanner
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * Persistence port for the user's registered scanners. Defined in commonMain so the repository is
 * platform-agnostic; the Android app backs it with DataStore
 * (`ee.schimke.openscan.app.data.DataStoreScannerStore`), tests use an in-memory implementation.
 */
interface ScannerStore {
  /** The saved scanners, newest edits reflected reactively. */
  val scanners: Flow<List<Scanner>>

  suspend fun upsert(scanner: Scanner)

  suspend fun remove(id: String)

  /** In-memory store, handy for tests, previews and desktop. */
  class InMemory(initial: List<Scanner> = emptyList()) : ScannerStore {
    private val state = MutableStateFlow(initial.associateBy { it.id.value })
    override val scanners: Flow<List<Scanner>> = state.map { it.values.toList() }

    override suspend fun upsert(scanner: Scanner) {
      state.value = state.value + (scanner.id.value to scanner)
    }

    override suspend fun remove(id: String) {
      state.value = state.value - id
    }
  }
}
