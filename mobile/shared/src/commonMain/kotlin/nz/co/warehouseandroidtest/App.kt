package nz.co.warehouseandroidtest

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import nz.co.warehouseandroidtest.ui.dashboard.DashboardScreen
import nz.co.warehouseandroidtest.ui.dashboard.DashboardViewModel
import nz.co.warehouseandroidtest.ui.navigation.AppNavHost
import nz.co.warehouseandroidtest.ui.theme.WarehouseTheme

@Composable
fun App() {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components {
                add(KtorNetworkFetcherFactory())
            }
            .build()
    }
    WarehouseTheme {
        AppNavHost()
    }
}

@Preview
@Composable
private fun AppPreview() {
    WarehouseTheme {
        DashboardScreen(viewModel = DashboardViewModel())
    }
}
