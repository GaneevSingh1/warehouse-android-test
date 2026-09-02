package nz.co.warehouseandroidtest.di

import io.ktor.client.HttpClient
import nz.co.warehouseandroidtest.data.local.AuthLocalDataSource
import nz.co.warehouseandroidtest.data.remote.GeneratedApiConfig
import nz.co.warehouseandroidtest.data.remote.createWarehouseHttpClient
import nz.co.warehouseandroidtest.data.remote.installTwlTokenInterceptor
import nz.co.warehouseandroidtest.data.remote.login.LoginRemoteDataSource
import nz.co.warehouseandroidtest.data.remote.search.SearchRemoteDataSource
import nz.co.warehouseandroidtest.data.repository.LoginRepository
import nz.co.warehouseandroidtest.data.repository.SearchRepository
import nz.co.warehouseandroidtest.getPlatform
import org.koin.core.qualifier.named
import org.koin.dsl.module

val UnauthenticatedHttpClient = named("unauthenticated")
val AuthenticatedHttpClient = named("authenticated")

val dataModule = module {
    single<HttpClient>(UnauthenticatedHttpClient) {
        createWarehouseHttpClient(
            subscriptionKey = GeneratedApiConfig.SUBSCRIPTION_KEY,
            device = getPlatform().twlDeviceHeader,
        )
    }
    single<HttpClient>(AuthenticatedHttpClient) {
        val loginRepository: LoginRepository = get()
        createWarehouseHttpClient(
            subscriptionKey = GeneratedApiConfig.SUBSCRIPTION_KEY,
            device = getPlatform().twlDeviceHeader,
        ).installTwlTokenInterceptor { loginRepository.getToken().getOrNull() }
    }
    single { LoginRemoteDataSource(get(UnauthenticatedHttpClient)) }
    single { SearchRemoteDataSource(get(AuthenticatedHttpClient)) }
    single { AuthLocalDataSource() }
    single { LoginRepository(get(), get()) }
    single { SearchRepository(get()) }
}
