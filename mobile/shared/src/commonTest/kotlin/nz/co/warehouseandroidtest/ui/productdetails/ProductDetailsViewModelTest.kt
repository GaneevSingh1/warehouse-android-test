package nz.co.warehouseandroidtest.ui.productdetails

import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import nz.co.warehouseandroidtest.data.EMPTY_PRODUCT_RESPONSE_JSON
import nz.co.warehouseandroidtest.data.PRODUCT_RESPONSE_JSON
import nz.co.warehouseandroidtest.data.mockAuthenticatedHttpClient
import nz.co.warehouseandroidtest.data.product.ProductRemoteDataSource
import nz.co.warehouseandroidtest.data.product.ProductRepository

class ProductDetailsViewModelTest {

    @Test
    fun load_emitsSuccessWithProductDetails() = runTest {
        val viewModel = viewModel(PRODUCT_RESPONSE_JSON)

        val state = assertIs<ProductDetailsUiState.Success>(viewModel.uiState)
        assertEquals("R2820075", state.details.id)
        assertEquals("Living & Co Stacking Stool", state.details.name)
        assertEquals(15.0, state.details.price)
        assertEquals(70, state.details.stockOnHand)
    }

    @Test
    fun load_emitsErrorWhenProductMissing() = runTest {
        val viewModel = viewModel(EMPTY_PRODUCT_RESPONSE_JSON)

        val state = assertIs<ProductDetailsUiState.Error>(viewModel.uiState)
        assertEquals("Product not found", state.message)
    }

    @Test
    fun load_emitsErrorOnFailure() = runTest {
        val viewModel = ProductDetailsViewModel(
            productId = "R2820075",
            productRepository = ProductRepository(
                ProductRemoteDataSource(mockAuthenticatedHttpClient(status = HttpStatusCode.Unauthorized)),
            ),
            dispatcher = Dispatchers.Unconfined,
        )

        val state = assertIs<ProductDetailsUiState.Error>(viewModel.uiState)
        assertTrue(state.message.contains("401"))
    }

    @Test
    fun retry_reloadsProduct() = runTest {
        val viewModel = viewModel(PRODUCT_RESPONSE_JSON)
        assertIs<ProductDetailsUiState.Success>(viewModel.uiState)

        viewModel.retry()

        val state = assertIs<ProductDetailsUiState.Success>(viewModel.uiState)
        assertEquals("Living & Co Stacking Stool", state.details.name)
    }

    private fun viewModel(json: String) = ProductDetailsViewModel(
        productId = "R2820075",
        productRepository = ProductRepository(
            ProductRemoteDataSource(mockAuthenticatedHttpClient(json)),
        ),
        dispatcher = Dispatchers.Unconfined,
    )
}
