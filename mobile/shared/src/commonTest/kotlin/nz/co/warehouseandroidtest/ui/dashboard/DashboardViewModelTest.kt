package nz.co.warehouseandroidtest.ui.dashboard

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

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
    fun search_ignoresBlankQuery() {
        val viewModel = DashboardViewModel()
        viewModel.onQueryChange("   ")

        viewModel.search()

        assertNull(viewModel.submittedQuery)
    }

    @Test
    fun search_trimsAndSubmitsQuery() {
        val viewModel = DashboardViewModel()
        viewModel.onQueryChange("  milk  ")

        viewModel.search()

        assertEquals("milk", viewModel.query)
        assertEquals("milk", viewModel.submittedQuery)
    }
}
