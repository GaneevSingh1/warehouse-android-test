package nz.co.warehouseandroidtest.di

import nz.co.warehouseandroidtest.ui.dashboard.DashboardViewModel
import nz.co.warehouseandroidtest.ui.productlist.ProductListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val uiModule = module {
    viewModelOf(::DashboardViewModel)
    viewModel { parameters ->
        ProductListViewModel(
            query = parameters.get(),
            searchRepository = get(),
        )
    }
}
