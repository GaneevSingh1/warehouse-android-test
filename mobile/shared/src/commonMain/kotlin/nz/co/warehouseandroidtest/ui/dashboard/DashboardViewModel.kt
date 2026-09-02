package nz.co.warehouseandroidtest.ui.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed interface DashboardEvent {
    data class SearchSubmitted(val query: String) : DashboardEvent
}

class DashboardViewModel : ViewModel() {
    var query: String by mutableStateOf("")
        private set

    private val eventsFlow = MutableSharedFlow<DashboardEvent>()
    val events: SharedFlow<DashboardEvent> = eventsFlow.asSharedFlow()

    private val logger = Logger.withTag("DashboardViewModel")

    fun onQueryChange(value: String) {
        query = value
    }

    fun clearQuery() {
        query = ""
    }

    fun onSearch(query: String) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return
        this.query = trimmed
        val emitted = eventsFlow.tryEmit(DashboardEvent.SearchSubmitted(trimmed))
        if (!emitted) {
            logger.e { "Failed to emit search submitted event for query \"$trimmed\"" }
        }
    }
}
