package nz.co.warehouseandroidtest.ui.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {
    var query: String by mutableStateOf("")
        private set

    var submittedQuery: String? by mutableStateOf(null)
        private set

    fun onQueryChange(value: String) {
        query = value
    }

    fun clearQuery() {
        query = ""
    }

    fun search(): String? {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return null
        query = trimmed
        submittedQuery = trimmed
        return trimmed
    }
}
