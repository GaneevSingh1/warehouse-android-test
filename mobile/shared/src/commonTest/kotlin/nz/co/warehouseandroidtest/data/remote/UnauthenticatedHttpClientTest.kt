package nz.co.warehouseandroidtest.data.remote

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import nz.co.warehouseandroidtest.data.HEADER_SUBSCRIPTION_KEY
import nz.co.warehouseandroidtest.data.HEADER_TWL_DEVICE
import nz.co.warehouseandroidtest.data.HEADER_TWL_TOKEN
import nz.co.warehouseandroidtest.data.LOGIN_RESPONSE_JSON
import nz.co.warehouseandroidtest.data.LOGIN_TOKEN
import nz.co.warehouseandroidtest.data.LOGIN_TOKEN_EXPIRES
import nz.co.warehouseandroidtest.data.login.LOGIN_URL
import nz.co.warehouseandroidtest.data.login.LoginRemoteDataSource
import nz.co.warehouseandroidtest.data.mockUnauthenticatedHttpClient

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

        val result = LoginRemoteDataSource(httpClient).login()

        assertTrue(result.isSuccess)
        assertEquals(LOGIN_URL, capturedUrl)
        assertEquals("Guest", capturedAuthorization)
        assertEquals("Android", capturedDevice)
        assertEquals("test-subscription-key", capturedSubscriptionKey)
    }

    @Test
    fun login_readsTokenAndExpiryHeaders() = runTest {
        val session = LoginRemoteDataSource(
            mockUnauthenticatedHttpClient(LOGIN_RESPONSE_JSON),
        ).login().getOrThrow()

        assertEquals(LOGIN_TOKEN, session.token)
        assertEquals(LOGIN_TOKEN_EXPIRES, session.expiresDatetime)
    }

    @Test
    fun login_returnsFailureOnHttpErrorInsteadOfMissingHeader() = runTest {
        val result = LoginRemoteDataSource(
            mockUnauthenticatedHttpClient(status = HttpStatusCode.Unauthorized),
        ).login()

        assertTrue(result.isFailure)
        val message = result.exceptionOrNull()?.message.orEmpty()
        assertTrue(message.contains("401"))
        assertTrue(message.contains("URL: $LOGIN_URL"))
        assertTrue(message.contains("Headers:"))
        assertTrue(message.contains("Body:"))
        assertFalse(message.contains("missing ${HEADER_TWL_TOKEN}"))
    }
}
