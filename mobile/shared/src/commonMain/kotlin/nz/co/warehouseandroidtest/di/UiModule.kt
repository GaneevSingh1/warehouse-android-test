package nz.co.warehouseandroidtest.di

import nz.co.warehouseandroidtest.ui.dashboard.DashboardViewModel
import nz.co.warehouseandroidtest.ui.productdetails.ProductDetailsViewModel
import nz.co.warehouseandroidtest.ui.search.ProductListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val uiModule = module {
    viewModel { DashboardViewModel(searchRepository = get()) }
    viewModel { parameters ->
        ProductListViewModel(
            query = parameters.get(),
            searchRepository = get(),
        )
    }
    viewModel { parameters ->
        ProductDetailsViewModel(
            productId = parameters.get(),
            productRepository = get(),
        )
    }
}
