package nz.co.warehouseandroidtest.ui.dashboard

import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import nz.co.warehouseandroidtest.data.EMPTY_SEARCH_RESPONSE_JSON
import nz.co.warehouseandroidtest.data.SEARCH_RESPONSE_JSON
import nz.co.warehouseandroidtest.data.mockAuthenticatedHttpClient
import nz.co.warehouseandroidtest.data.search.SearchRemoteDataSource
import nz.co.warehouseandroidtest.data.search.SearchRepository

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    @Test
    fun onSearch_ignoresBlankQuery() = runTest {
        val viewModel = viewModel()
        val received = mutableListOf<DashboardEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.events.collect { received += it }
        }

        viewModel.onSearch("   ")

        assertEquals(emptyList(), received)
    }

    @Test
    fun onSearch_trimsQueryAndEmitsSearchSubmitted() = runTest {
        val viewModel = viewModel()
        val received = mutableListOf<DashboardEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.events.collect { received += it }
        }

        viewModel.onSearch("  milk  ")

        assertEquals(DashboardEvent.SearchSubmitted("milk"), received.single())
    }

    @Test
    fun load_requestsFathersDayWithLimit10() = runTest {
        val searches = mutableListOf<String>()
        val limits = mutableListOf<String>()
        val viewModel = viewModel(SEARCH_RESPONSE_JSON) { request ->
            searches += request.url.parameters["Search"].orEmpty()
            limits += request.url.parameters["Limit"].orEmpty()
        }

        assertIs<FathersDayUiState.Success>(viewModel.fathersDayUiState)
        assertEquals(listOf(FATHERS_DAY_QUERY), searches)
        assertEquals(listOf(FATHERS_DAY_LIMIT.toString()), limits)
    }

    @Test
    fun load_emitsSuccessWithProducts() = runTest {
        val viewModel = viewModel(SEARCH_RESPONSE_JSON)

        val state = assertIs<FathersDayUiState.Success>(viewModel.fathersDayUiState)
        assertEquals(3, state.products.size)
        assertEquals("Living & Co Stacking Stool", state.products.first().name)
    }

    @Test
    fun load_emitsEmptyWhenNoProducts() = runTest {
        val viewModel = viewModel(EMPTY_SEARCH_RESPONSE_JSON)

        assertEquals(FathersDayUiState.Empty, viewModel.fathersDayUiState)
    }

    @Test
    fun load_emitsErrorOnFailure() = runTest {
        val viewModel = DashboardViewModel(
            searchRepository = SearchRepository(
                SearchRemoteDataSource(mockAuthenticatedHttpClient(status = HttpStatusCode.Unauthorized)),
            ),
            dispatcher = Dispatchers.Unconfined,
        )

        assertEquals(FathersDayUiState.Error, viewModel.fathersDayUiState)
    }

    @Test
    fun retryFathersDay_reloadsProducts() = runTest {
        val searches = mutableListOf<String>()
        val viewModel = viewModel(EMPTY_SEARCH_RESPONSE_JSON) { request ->
            searches += request.url.parameters["Search"].orEmpty()
        }
        assertEquals(FathersDayUiState.Empty, viewModel.fathersDayUiState)

        viewModel.retryFathersDay()

        assertEquals(FathersDayUiState.Empty, viewModel.fathersDayUiState)
        assertEquals(listOf(FATHERS_DAY_QUERY, FATHERS_DAY_QUERY), searches)
    }

    private fun viewModel(
        json: String = SEARCH_RESPONSE_JSON,
        onRequest: (HttpRequestData) -> Unit = {},
    ) = DashboardViewModel(
        searchRepository = SearchRepository(
            SearchRemoteDataSource(mockAuthenticatedHttpClient(json, onRequest = onRequest)),
        ),
        dispatcher = Dispatchers.Unconfined,
    )
}
