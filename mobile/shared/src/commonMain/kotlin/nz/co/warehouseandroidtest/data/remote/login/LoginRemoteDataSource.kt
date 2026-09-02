package nz.co.warehouseandroidtest.data.remote.login

import io.ktor.client.HttpClient
import nz.co.warehouseandroidtest.data.remote.HEADER_TWL_TOKEN
import nz.co.warehouseandroidtest.data.remote.HEADER_TWL_TOKEN_EXPIRES
import nz.co.warehouseandroidtest.data.remote.getRelative
import nz.co.warehouseandroidtest.domain.model.LoginSession

internal const val LOGIN_PATH = "/Login.json"

class LoginRemoteDataSource(
    private val httpClient: HttpClient,
) {
    suspend fun login(): LoginSession {
        val response = httpClient.getRelative(LOGIN_PATH)
        val token = response.headers[HEADER_TWL_TOKEN]
            ?: error("Login response missing $HEADER_TWL_TOKEN header")
        val expiresDatetime = response.headers[HEADER_TWL_TOKEN_EXPIRES]
            ?: error("Login response missing $HEADER_TWL_TOKEN_EXPIRES header")
        return LoginSession(
            token = token,
            expiresDatetime = expiresDatetime,
        )
    }
}
