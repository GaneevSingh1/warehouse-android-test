package nz.co.warehouseandroidtest.di

import nz.co.warehouseandroidtest.data.local.LoginLocalDataSource
import nz.co.warehouseandroidtest.data.remote.UnauthenticatedApiClient
import nz.co.warehouseandroidtest.data.remote.login.LoginRemoteDataSource
import nz.co.warehouseandroidtest.data.repository.LoginRepository
import nz.co.warehouseandroidtest.getPlatform
import nz.co.warehouseandroidtest.ui.dashboard.DashboardViewModel
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    if (GlobalContext.getOrNull() != null) return
    startKoin {
        appDeclaration()
        modules(sharedModule, platformModule)
    }
}

val sharedModule = module {
    single {
        UnauthenticatedApiClient.create(device = getPlatform().twlDeviceHeader)
    }
    single { LoginRemoteDataSource(get()) }
    single { LoginLocalDataSource(get()) }
    single { LoginRepository(get(), get()) }
    viewModelOf(::DashboardViewModel)
}

expect val platformModule: Module
