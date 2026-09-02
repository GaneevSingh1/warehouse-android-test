package nz.co.warehouseandroidtest.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

sealed interface DashboardEvent {
    data class SearchSubmitted(val query: String) : DashboardEvent
}

class DashboardViewModel(
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main,
) : ViewModel() {
    private val eventsFlow = MutableSharedFlow<DashboardEvent>()
    val events: SharedFlow<DashboardEvent> = eventsFlow.asSharedFlow()

    fun onSearch(query: String) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch(dispatcher) {
            eventsFlow.emit(DashboardEvent.SearchSubmitted(trimmed))
        }
    }
}
