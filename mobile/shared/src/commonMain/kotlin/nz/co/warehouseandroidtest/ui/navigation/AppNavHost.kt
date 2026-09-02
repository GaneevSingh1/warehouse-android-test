package nz.co.warehouseandroidtest.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import nz.co.warehouseandroidtest.ui.dashboard.DashboardScreen
import nz.co.warehouseandroidtest.ui.productlist.ProductListScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = DashboardRoute,
        modifier = modifier.fillMaxSize(),
    ) {
        composable<DashboardRoute> {
            DashboardScreen(
                onSearchSubmitted = { query ->
                    navController.navigate(ProductListRoute(query))
                },
            )
        }
        composable<ProductListRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<ProductListRoute>()
            ProductListScreen(
                query = route.query,
                onBack = { navController.navigateUp() },
            )
        }
    }
}
