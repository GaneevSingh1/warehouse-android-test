package nz.co.warehouseandroidtest.data.remote.login

import io.ktor.client.HttpClient
import nz.co.warehouseandroidtest.data.remote.HEADER_TWL_TOKEN
import nz.co.warehouseandroidtest.data.remote.HEADER_TWL_TOKEN_EXPIRES
import nz.co.warehouseandroidtest.data.remote.getResult
import nz.co.warehouseandroidtest.domain.model.LoginSession

internal const val LOGIN_URL = "https://legacy-apim.twg.co.nz/twgCSharpTest/Login.json"

class LoginRemoteDataSource(
    private val httpClient: HttpClient,
) {
    suspend fun login(): Result<LoginSession> = httpClient.getResult(LOGIN_URL) { response ->
        val token = response.headers[HEADER_TWL_TOKEN]
            ?: error("Login response missing $HEADER_TWL_TOKEN header")
        val expiresDatetime = response.headers[HEADER_TWL_TOKEN_EXPIRES]
            ?: error("Login response missing $HEADER_TWL_TOKEN_EXPIRES header")
        LoginSession(
            token = token,
            expiresDatetime = expiresDatetime,
        )
    }
}
