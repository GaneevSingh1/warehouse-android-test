package nz.co.warehouseandroidtest.ui.dashboard

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    @Test
    fun onSearch_ignoresBlankQuery() = runTest {
        val viewModel = DashboardViewModel(Dispatchers.Unconfined)
        val received = mutableListOf<DashboardEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.events.collect { received += it }
        }

        viewModel.onSearch("   ")

        assertEquals(emptyList(), received)
    }

    @Test
    fun onSearch_trimsQueryAndEmitsSearchSubmitted() = runTest {
        val viewModel = DashboardViewModel(Dispatchers.Unconfined)
        val received = mutableListOf<DashboardEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.events.collect { received += it }
        }

        viewModel.onSearch("  milk  ")

        assertEquals(DashboardEvent.SearchSubmitted("milk"), received.single())
    }
}
