package nz.co.warehouseandroidtest.ui.search

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
import kotlinx.coroutines.launch
import nz.co.warehouseandroidtest.data.search.SearchRepository
import nz.co.warehouseandroidtest.domain.search.DEFAULT_SEARCH_LIMIT
import nz.co.warehouseandroidtest.domain.search.DEFAULT_SEARCH_START
import nz.co.warehouseandroidtest.domain.search.SearchResult

sealed interface ProductListUiState {
    data object Loading : ProductListUiState
    data class Success(val result: SearchResult) : ProductListUiState
    data object Empty : ProductListUiState
    data object Error : ProductListUiState
}

class ProductListViewModel(
    val query: String,
    private val searchRepository: SearchRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {
    var uiState: ProductListUiState by mutableStateOf(ProductListUiState.Loading)
        private set

    var start: Int by mutableStateOf(DEFAULT_SEARCH_START)
        private set

    var canGoPrevious: Boolean by mutableStateOf(false)
        private set

    var canGoNext: Boolean by mutableStateOf(false)
        private set

    private var loadJob: Job? = null

    init {
        loadProducts()
    }

    fun retry() {
        loadProducts()
    }

    fun nextPage() {
        if (uiState is ProductListUiState.Loading || !canGoNext) return
        start += DEFAULT_SEARCH_LIMIT
        loadProducts()
    }

    fun previousPage() {
        if (uiState is ProductListUiState.Loading || !canGoPrevious) return
        start = (start - DEFAULT_SEARCH_LIMIT).coerceAtLeast(DEFAULT_SEARCH_START)
        loadProducts()
    }

    private fun loadProducts() {
        loadJob?.cancel()
        uiState = ProductListUiState.Loading
        val requestStart = start
        loadJob = viewModelScope.launch(dispatcher) {
            searchRepository.search(query, start = requestStart, limit = DEFAULT_SEARCH_LIMIT)
                .onSuccess { result ->
                    ensureActive()
                    updatePaging(result, requestStart)
                    uiState = if (result.products.isEmpty()) {
                        ProductListUiState.Empty
                    } else {
                        ProductListUiState.Success(result)
                    }
                }
                .onFailure {
                    ensureActive()
                    canGoNext = false
                    uiState = ProductListUiState.Error
                }
        }
    }

    private fun updatePaging(result: SearchResult, pageStart: Int) {
        canGoPrevious = pageStart > DEFAULT_SEARCH_START
        canGoNext = pageStart + DEFAULT_SEARCH_LIMIT < result.total
    }
}
