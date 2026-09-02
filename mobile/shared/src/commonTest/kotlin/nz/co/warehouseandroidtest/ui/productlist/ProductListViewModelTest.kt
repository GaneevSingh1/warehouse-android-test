package nz.co.warehouseandroidtest.ui.productlist

import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import nz.co.warehouseandroidtest.data.EMPTY_SEARCH_RESPONSE_JSON
import nz.co.warehouseandroidtest.data.SEARCH_RESPONSE_JSON
import nz.co.warehouseandroidtest.data.mockAuthenticatedHttpClient
import nz.co.warehouseandroidtest.data.remote.search.SearchRemoteDataSource
import nz.co.warehouseandroidtest.data.repository.SearchRepository

class ProductListViewModelTest {

    @Test
    fun load_emitsSuccessWithProducts() = runTest {
        val viewModel = viewModel(SEARCH_RESPONSE_JSON)

        val state = assertIs<ProductListUiState.Success>(viewModel.uiState)
        assertEquals(64, state.result.total)
        assertEquals(3, state.result.products.size)
        assertEquals("Living & Co Stacking Stool", state.result.products.first().name)
    }

    @Test
    fun load_emitsEmptyWhenNoProducts() = runTest {
        val viewModel = viewModel(EMPTY_SEARCH_RESPONSE_JSON)

        assertEquals(ProductListUiState.Empty, viewModel.uiState)
    }

    @Test
    fun load_emitsErrorOnFailure() = runTest {
        val viewModel = ProductListViewModel(
            query = "stool",
            searchRepository = SearchRepository(
                SearchRemoteDataSource(mockAuthenticatedHttpClient(status = HttpStatusCode.Unauthorized)),
            ),
            dispatcher = Dispatchers.Unconfined,
        )

        val state = assertIs<ProductListUiState.Error>(viewModel.uiState)
        assertTrue(state.message.contains("401"))
    }

    @Test
    fun retry_reloadsProducts() = runTest {
        val viewModel = viewModel(EMPTY_SEARCH_RESPONSE_JSON)
        assertEquals(ProductListUiState.Empty, viewModel.uiState)

        viewModel.retry()

        assertEquals(ProductListUiState.Empty, viewModel.uiState)
    }

    @Test
    fun formatPrice_rendersTwoDecimalPlaces() {
        assertEquals("$15.00", formatPrice(15.0))
        assertEquals("$20.50", formatPrice(20.5))
        assertEquals("$9.99", formatPrice(9.99))
    }

    private fun viewModel(json: String) = ProductListViewModel(
        query = "stool",
        searchRepository = SearchRepository(
            SearchRemoteDataSource(mockAuthenticatedHttpClient(json)),
        ),
        dispatcher = Dispatchers.Unconfined,
    )
}
