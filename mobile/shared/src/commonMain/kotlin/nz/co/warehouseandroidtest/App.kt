package nz.co.warehouseandroidtest

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import nz.co.warehouseandroidtest.ui.dashboard.DashboardScreen
import nz.co.warehouseandroidtest.ui.theme.WarehouseTheme

@Composable
@Preview
fun App() {
    WarehouseTheme {
        DashboardScreen()
    }
}
