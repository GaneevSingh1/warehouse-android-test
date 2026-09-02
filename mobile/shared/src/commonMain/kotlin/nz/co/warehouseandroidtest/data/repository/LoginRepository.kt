package nz.co.warehouseandroidtest.data.repository

import nz.co.warehouseandroidtest.data.local.AuthLocalDataSource
import nz.co.warehouseandroidtest.data.remote.login.LoginRemoteDataSource

class LoginRepository(
    private val remoteDataSource: LoginRemoteDataSource,
    private val localDataSource: AuthLocalDataSource,
) {
    suspend fun getToken(): Result<String> {
        localDataSource.getLoginSession()?.token?.takeIf { it.isNotBlank() }?.let { token ->
            return Result.success(token)
        }
        return remoteDataSource.login().onSuccess { session ->
            localDataSource.saveLoginSession(session)
        }.map { it.token }
    }
}
