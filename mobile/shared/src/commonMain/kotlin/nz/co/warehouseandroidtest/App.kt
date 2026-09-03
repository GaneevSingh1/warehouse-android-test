package nz.co.warehouseandroidtest

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import nz.co.warehouseandroidtest.ui.dashboard.DashboardScreen
import nz.co.warehouseandroidtest.ui.dashboard.DashboardViewModel
import nz.co.warehouseandroidtest.ui.navigation.AppNavHost
import nz.co.warehouseandroidtest.ui.theme.WarehouseTheme

@Composable
fun App() {
    remember { SingletonImageLoader.setSafe(::createImageLoader) }
    WarehouseTheme {
        AppNavHost()
    }
}

private fun createImageLoader(context: PlatformContext): ImageLoader =
    ImageLoader.Builder(context)
        .addPlatformNetworkFetcher()
        .build()

@Preview
@Composable
private fun AppPreview() {
    WarehouseTheme {
        DashboardScreen(viewModel = DashboardViewModel())
    }
}
