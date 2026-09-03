package nz.co.warehouseandroidtest.ui.productdetails

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
import nz.co.warehouseandroidtest.data.product.ProductRepository
import nz.co.warehouseandroidtest.domain.product.ProductDetails

sealed interface ProductDetailsUiState {
    data object Loading : ProductDetailsUiState
    data class Success(val details: ProductDetails) : ProductDetailsUiState
    data class Error(val message: String) : ProductDetailsUiState
}

class ProductDetailsViewModel(
    val productId: String,
    private val productRepository: ProductRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {
    var uiState: ProductDetailsUiState by mutableStateOf(ProductDetailsUiState.Loading)
        private set

    private var loadJob: Job? = null

    init {
        loadProduct()
    }

    fun retry() {
        loadProduct()
    }

    private fun loadProduct() {
        loadJob?.cancel()
        uiState = ProductDetailsUiState.Loading
        loadJob = viewModelScope.launch(dispatcher) {
            productRepository.getProduct(productId)
                .onSuccess { details ->
                    ensureActive()
                    uiState = ProductDetailsUiState.Success(details)
                }
                .onFailure { error ->
                    ensureActive()
                    uiState = ProductDetailsUiState.Error(
                        error.message
                            ?.lineSequence()
                            ?.firstOrNull()
                            ?.takeIf { it.isNotBlank() }
                            .orEmpty(),
                    )
                }
        }
    }
}
