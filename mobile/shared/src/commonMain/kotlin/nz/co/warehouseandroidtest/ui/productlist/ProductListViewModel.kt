package nz.co.warehouseandroidtest.ui.productlist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nz.co.warehouseandroidtest.data.remote.search.DEFAULT_SEARCH_LIMIT
import nz.co.warehouseandroidtest.data.remote.search.DEFAULT_SEARCH_START
import nz.co.warehouseandroidtest.data.repository.SearchRepository
import nz.co.warehouseandroidtest.domain.model.SearchResult

sealed interface ProductListUiState {
    data object Loading : ProductListUiState
    data class Success(val result: SearchResult) : ProductListUiState
    data object Empty : ProductListUiState
    data class Error(val message: String) : ProductListUiState
}

class ProductListViewModel(
    val query: String,
    private val searchRepository: SearchRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main,
) : ViewModel() {
    var uiState: ProductListUiState by mutableStateOf(ProductListUiState.Loading)
        private set

    var start: Int by mutableStateOf(DEFAULT_SEARCH_START)
        private set

    var canGoPrevious: Boolean by mutableStateOf(false)
        private set

    var canGoNext: Boolean by mutableStateOf(false)
        private set

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
        viewModelScope.launch(dispatcher) {
            uiState = ProductListUiState.Loading
            searchRepository.search(query, start = start, limit = DEFAULT_SEARCH_LIMIT)
                .onSuccess { result ->
                    updatePaging(result)
                    uiState = if (result.products.isEmpty()) {
                        ProductListUiState.Empty
                    } else {
                        ProductListUiState.Success(result)
                    }
                }
                .onFailure { error ->
                    canGoNext = false
                    uiState = ProductListUiState.Error(
                        error.message?.takeIf { it.isNotBlank() } ?: "Couldn't load products",
                    )
                }
        }
    }

private fun updatePaging(result: SearchResult) {
    canGoPrevious = start > DEFAULT_SEARCH_START
    canGoNext = start + DEFAULT_SEARCH_LIMIT < result.total
}
