package nz.co.warehouseandroidtest.ui.dashboard

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    @Test
    fun onQueryChange_updatesQuery() {
        val viewModel = DashboardViewModel()

        viewModel.onQueryChange("milk")

        assertEquals("milk", viewModel.query)
    }

    @Test
    fun clearQuery_resetsQuery() {
        val viewModel = DashboardViewModel()
        viewModel.onQueryChange("milk")

        viewModel.clearQuery()

        assertEquals("", viewModel.query)
    }

    @Test
    fun onSearch_ignoresBlankQuery() = runTest {
        val viewModel = DashboardViewModel()
        val received = mutableListOf<DashboardEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.events.collect { received += it }
        }

        viewModel.onSearch("   ")

        assertEquals(emptyList(), received)
    }

    @Test
    fun onSearch_trimsQueryAndEmitsSearchSubmitted() = runTest {
        val viewModel = DashboardViewModel()
        val received = mutableListOf<DashboardEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.events.collect { received += it }
        }

        viewModel.onSearch("  milk  ")

        assertEquals("milk", viewModel.query)
        assertEquals(DashboardEvent.SearchSubmitted("milk"), received.single())
    }
}
