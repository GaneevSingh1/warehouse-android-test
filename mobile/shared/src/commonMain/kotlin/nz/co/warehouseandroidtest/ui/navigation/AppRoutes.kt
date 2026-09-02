package nz.co.warehouseandroidtest.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data object DashboardRoute

@Serializable
data class ProductListRoute(val query: String)
