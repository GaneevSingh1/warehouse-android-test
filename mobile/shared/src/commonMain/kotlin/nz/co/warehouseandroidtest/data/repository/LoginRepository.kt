package nz.co.warehouseandroidtest.data.repository

import nz.co.warehouseandroidtest.data.local.LoginLocalDataSource
import nz.co.warehouseandroidtest.data.remote.login.LoginRemoteDataSource
import nz.co.warehouseandroidtest.domain.model.LoginSession

class LoginRepository(
    private val remoteDataSource: LoginRemoteDataSource,
    private val localDataSource: LoginLocalDataSource,
) {
    suspend fun login(): LoginSession {
        val session = remoteDataSource.login()
        localDataSource.save(session)
        return session
    }

    suspend fun getCachedSession(): LoginSession? = localDataSource.get()
}
