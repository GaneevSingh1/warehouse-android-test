package nz.co.warehouseandroidtest.ui.productlist

import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import nz.co.warehouseandroidtest.data.EMPTY_SEARCH_RESPONSE_JSON
import nz.co.warehouseandroidtest.data.SEARCH_RESPONSE_JSON
import nz.co.warehouseandroidtest.data.mockAuthenticatedHttpClient
import nz.co.warehouseandroidtest.data.remote.search.SearchRemoteDataSource
import nz.co.warehouseandroidtest.data.repository.SearchRepository
import nz.co.warehouseandroidtest.domain.model.DEFAULT_SEARCH_LIMIT

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

        assertEquals(ProductListUiState.Error, viewModel.uiState)
    }

    @Test
    fun retry_reloadsProducts() = runTest {
        val viewModel = viewModel(EMPTY_SEARCH_RESPONSE_JSON)
        assertEquals(ProductListUiState.Empty, viewModel.uiState)

        viewModel.retry()

        assertEquals(ProductListUiState.Empty, viewModel.uiState)
    }

    @Test
    fun nextPage_requestsNextStart() = runTest {
        val starts = mutableListOf<String>()
        val viewModel = viewModel(SEARCH_RESPONSE_JSON) { request ->
            starts += request.url.parameters["Start"].orEmpty()
        }

        assertEquals(listOf("0"), starts)
        assertTrue(viewModel.canGoNext)
        assertFalse(viewModel.canGoPrevious)

        viewModel.nextPage()

        assertEquals(listOf("0", DEFAULT_SEARCH_LIMIT.toString()), starts)
        assertEquals(DEFAULT_SEARCH_LIMIT, viewModel.start)
        assertTrue(viewModel.canGoPrevious)
    }

    @Test
    fun previousPage_requestsPreviousStart() = runTest {
        val starts = mutableListOf<String>()
        val viewModel = viewModel(SEARCH_RESPONSE_JSON) { request ->
            starts += request.url.parameters["Start"].orEmpty()
        }

        viewModel.nextPage()
        viewModel.previousPage()

        assertEquals(listOf("0", DEFAULT_SEARCH_LIMIT.toString(), "0"), starts)
        assertEquals(0, viewModel.start)
        assertFalse(viewModel.canGoPrevious)
    }

    @Test
    fun previousPage_doesNothingOnFirstPage() = runTest {
        val starts = mutableListOf<String>()
        val viewModel = viewModel(SEARCH_RESPONSE_JSON) { request ->
            starts += request.url.parameters["Start"].orEmpty()
        }

        viewModel.previousPage()

        assertEquals(listOf("0"), starts)
    }

    @Test
    fun nextPage_doesNothingWhenNoMoreResults() = runTest {
        val starts = mutableListOf<String>()
        val viewModel = viewModel(EMPTY_SEARCH_RESPONSE_JSON) { request ->
            starts += request.url.parameters["Start"].orEmpty()
        }

        viewModel.nextPage()

        assertEquals(listOf("0"), starts)
        assertFalse(viewModel.canGoNext)
    }

    @Test
    fun formatPrice_rendersTwoDecimalPlaces() {
        assertEquals("$15.00", formatPrice(15.0))
        assertEquals("$20.50", formatPrice(20.5))
        assertEquals("$9.99", formatPrice(9.99))
    }

    private fun viewModel(
        json: String,
        onRequest: (HttpRequestData) -> Unit = {},
    ) = ProductListViewModel(
        query = "stool",
        searchRepository = SearchRepository(
            SearchRemoteDataSource(mockAuthenticatedHttpClient(json, onRequest = onRequest)),
        ),
        dispatcher = Dispatchers.Unconfined,
    )
}
