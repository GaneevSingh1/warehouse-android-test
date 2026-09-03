package nz.co.warehouseandroidtest.di

import io.ktor.client.HttpClient
import nz.co.warehouseandroidtest.data.login.AuthLocalDataSource
import nz.co.warehouseandroidtest.data.login.LoginRemoteDataSource
import nz.co.warehouseandroidtest.data.login.LoginRepository
import nz.co.warehouseandroidtest.data.remote.GeneratedApiConfig
import nz.co.warehouseandroidtest.data.createWarehouseHttpClient
import nz.co.warehouseandroidtest.data.installTwlTokenInterceptor
import nz.co.warehouseandroidtest.data.search.SearchRemoteDataSource
import nz.co.warehouseandroidtest.data.search.SearchRepository
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
        ).installTwlTokenInterceptor(
            tokenProvider = { loginRepository.getToken().getOrThrow() },
            onUnauthorized = loginRepository::invalidateSession,
        )
    }
    single { LoginRemoteDataSource(get(UnauthenticatedHttpClient)) }
    single { SearchRemoteDataSource(get(AuthenticatedHttpClient)) }
    single { AuthLocalDataSource() }
    single { LoginRepository(get(), get()) }
    single { SearchRepository(get()) }
}
