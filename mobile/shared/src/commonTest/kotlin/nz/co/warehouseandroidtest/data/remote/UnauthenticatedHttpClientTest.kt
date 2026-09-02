package nz.co.warehouseandroidtest.data.remote

import io.ktor.http.HttpHeaders
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest
import nz.co.warehouseandroidtest.data.LOGIN_RESPONSE_JSON
import nz.co.warehouseandroidtest.data.LOGIN_TOKEN
import nz.co.warehouseandroidtest.data.LOGIN_TOKEN_EXPIRES
import nz.co.warehouseandroidtest.data.mockUnauthenticatedHttpClient
import nz.co.warehouseandroidtest.data.remote.login.LOGIN_PATH
import nz.co.warehouseandroidtest.data.remote.login.LoginRemoteDataSource

class UnauthenticatedHttpClientTest {

    @Test
    fun login_sendsGuestHeadersAndPath() = runTest {
        var capturedUrl = ""
        var capturedAuthorization = ""
        var capturedDevice = ""
        var capturedSubscriptionKey = ""

        val httpClient = mockUnauthenticatedHttpClient { request ->
            capturedUrl = request.url.toString()
            capturedAuthorization = request.headers[HttpHeaders.Authorization].orEmpty()
            capturedDevice = request.headers[HEADER_TWL_DEVICE].orEmpty()
            capturedSubscriptionKey = request.headers[HEADER_SUBSCRIPTION_KEY].orEmpty()
        }

        LoginRemoteDataSource(httpClient).login()

        assertEquals("https://legacy-apim.twg.co.nz/twgCSharpTest$LOGIN_PATH", capturedUrl)
        assertEquals("Guest", capturedAuthorization)
        assertEquals("Android", capturedDevice)
        assertEquals("test-subscription-key", capturedSubscriptionKey)
    }

    @Test
    fun login_readsTokenAndExpiryHeaders() = runTest {
        val session = LoginRemoteDataSource(
            mockUnauthenticatedHttpClient(LOGIN_RESPONSE_JSON),
        ).login()

        assertEquals(LOGIN_TOKEN, session.token)
        assertEquals(LOGIN_TOKEN_EXPIRES, session.expiresDatetime)
    }
}
