package nz.co.warehouseandroidtest.data.repository

import nz.co.warehouseandroidtest.data.local.AuthLocalDataSource
import nz.co.warehouseandroidtest.data.remote.login.LoginRemoteDataSource
import nz.co.warehouseandroidtest.domain.model.LoginSession

class LoginRepository(
    private val remoteDataSource: LoginRemoteDataSource,
    private val localDataSource: AuthLocalDataSource,
) {
    suspend fun login(): Result<LoginSession> = remoteDataSource.login().onSuccess { session ->
        localDataSource.save(session)
    }

    suspend fun getCachedSession(): LoginSession? = localDataSource.get()
}
