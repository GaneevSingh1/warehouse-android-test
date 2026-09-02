package nz.co.warehouseandroidtest

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import nz.co.warehouseandroidtest.data.repository.LoginRepository
import nz.co.warehouseandroidtest.ui.dashboard.DashboardScreen
import nz.co.warehouseandroidtest.ui.dashboard.DashboardViewModel
import nz.co.warehouseandroidtest.ui.theme.WarehouseTheme
import org.koin.compose.koinInject

@Composable
fun App() {
    val loginRepository: LoginRepository = koinInject()

    LaunchedEffect(loginRepository) {
        runCatching { loginRepository.ensureSession() }
    }

    WarehouseTheme {
        DashboardScreen()
    }
}

@Preview
@Composable
private fun AppPreview() {
    WarehouseTheme {
        DashboardScreen(viewModel = DashboardViewModel())
    }
}
