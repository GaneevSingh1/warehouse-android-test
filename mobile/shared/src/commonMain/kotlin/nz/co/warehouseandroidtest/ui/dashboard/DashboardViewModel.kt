package nz.co.warehouseandroidtest.ui.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import nz.co.warehouseandroidtest.data.search.SearchRepository
import nz.co.warehouseandroidtest.domain.search.Product

internal const val FATHERS_DAY_QUERY = "fathers day"
internal const val FATHERS_DAY_LIMIT = 10

sealed interface DashboardEvent {
    data class SearchSubmitted(val query: String) : DashboardEvent
}

sealed interface FathersDayUiState {
    data object Loading : FathersDayUiState
    data class Success(val products: List<Product>) : FathersDayUiState
    data object Empty : FathersDayUiState
    data object Error : FathersDayUiState
}

class DashboardViewModel(
    private val searchRepository: SearchRepository? = null,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {
    private val eventsFlow = MutableSharedFlow<DashboardEvent>()
    val events: SharedFlow<DashboardEvent> = eventsFlow.asSharedFlow()

    var fathersDayUiState: FathersDayUiState by mutableStateOf(FathersDayUiState.Loading)
        private set

    private var loadJob: Job? = null

    init {
        loadFathersDayProducts()
    }

    fun onSearch(query: String) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch(dispatcher) {
            eventsFlow.emit(DashboardEvent.SearchSubmitted(trimmed))
        }
    }

    fun retryFathersDay() {
        loadFathersDayProducts()
    }

    private fun loadFathersDayProducts() {
        val repository = searchRepository
        if (repository == null) {
            fathersDayUiState = FathersDayUiState.Empty
            return
        }

        loadJob?.cancel()
        fathersDayUiState = FathersDayUiState.Loading
        loadJob = viewModelScope.launch(dispatcher) {
            repository.search(FATHERS_DAY_QUERY, start = 0, limit = FATHERS_DAY_LIMIT)
                .onSuccess { result ->
                    ensureActive()
                    val products = result.products.take(FATHERS_DAY_LIMIT)
                    fathersDayUiState = if (products.isEmpty()) {
                        FathersDayUiState.Empty
                    } else {
                        FathersDayUiState.Success(products)
                    }
                }
                .onFailure {
                    ensureActive()
                    fathersDayUiState = FathersDayUiState.Error
                }
        }
    }
}
