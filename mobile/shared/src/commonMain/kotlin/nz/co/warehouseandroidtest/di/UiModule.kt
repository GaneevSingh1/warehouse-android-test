package nz.co.warehouseandroidtest.di

import nz.co.warehouseandroidtest.ui.dashboard.DashboardViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val uiModule = module {
    viewModelOf(::DashboardViewModel)
}
