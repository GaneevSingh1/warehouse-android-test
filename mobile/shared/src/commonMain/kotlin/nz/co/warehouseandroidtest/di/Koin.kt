package nz.co.warehouseandroidtest.di

import io.ktor.client.HttpClient
import nz.co.warehouseandroidtest.data.local.LoginLocalDataSource
import nz.co.warehouseandroidtest.data.remote.GeneratedApiConfig
import nz.co.warehouseandroidtest.data.remote.createWarehouseHttpClient
import nz.co.warehouseandroidtest.data.remote.installTwlTokenInterceptor
import nz.co.warehouseandroidtest.data.remote.login.LoginRemoteDataSource
import nz.co.warehouseandroidtest.data.repository.LoginRepository
import nz.co.warehouseandroidtest.getPlatform
import nz.co.warehouseandroidtest.ui.dashboard.DashboardViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val UnauthenticatedHttpClient = named("unauthenticated")
val AuthenticatedHttpClient = named("authenticated")

val sharedModule = module {
    single<HttpClient>(UnauthenticatedHttpClient) {
        createWarehouseHttpClient(
            subscriptionKey = GeneratedApiConfig.SUBSCRIPTION_KEY,
            device = getPlatform().twlDeviceHeader,
        )
    }
    single<HttpClient>(AuthenticatedHttpClient) {
        val loginLocalDataSource: LoginLocalDataSource = get()
        createWarehouseHttpClient(
            subscriptionKey = GeneratedApiConfig.SUBSCRIPTION_KEY,
            device = getPlatform().twlDeviceHeader,
        ).installTwlTokenInterceptor { loginLocalDataSource.get()?.token }
    }
    single { LoginRemoteDataSource(get(UnauthenticatedHttpClient)) }
    single { LoginLocalDataSource(get()) }
    single { LoginRepository(get(), get()) }
    viewModelOf(::DashboardViewModel)
}

expect val platformModule: Module
