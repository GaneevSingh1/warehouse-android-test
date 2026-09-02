package nz.co.warehouseandroidtest.ui.productlist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

    init {
        loadProducts()
    }

    fun retry() {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch(dispatcher) {
            uiState = ProductListUiState.Loading
            searchRepository.search(query)
                .onSuccess { result ->
                    uiState = if (result.products.isEmpty()) {
                        ProductListUiState.Empty
                    } else {
                        ProductListUiState.Success(result)
                    }
                }
                .onFailure { error ->
                    uiState = ProductListUiState.Error(
                        error.message?.takeIf { it.isNotBlank() } ?: "Couldn't load products",
                    )
                }
        }
    }
}
