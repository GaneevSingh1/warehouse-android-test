package nz.co.warehouseandroidtest.data.login

import nz.co.warehouseandroidtest.domain.login.isExpired

class LoginRepository(
    private val remoteDataSource: LoginRemoteDataSource,
    private val localDataSource: AuthLocalDataSource,
) {
    suspend fun getToken(): Result<String> {
        cachedValidToken()?.let { token ->
            return Result.success(token)
        }
        return refreshToken()
    }

    fun invalidateSession() {
        localDataSource.clearLoginSession()
    }

    private fun cachedValidToken(): String? {
        val session = localDataSource.getLoginSession() ?: return null
        if (!session.isExpired()) {
            return session.token
        }
        localDataSource.clearLoginSession()
        return null
    }

    private suspend fun refreshToken(): Result<String> = remoteDataSource.login().mapCatching { session ->
        if (session.isExpired()) {
            error("Login returned an expired token")
        }
        localDataSource.saveLoginSession(session)
        session.token
    }
}
