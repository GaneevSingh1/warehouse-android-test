package nz.co.warehouseandroidtest.data.remote

import io.ktor.http.HttpHeaders
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import nz.co.warehouseandroidtest.data.LOGIN_RESPONSE_JSON
import nz.co.warehouseandroidtest.data.mockUnauthenticatedApiClient
import nz.co.warehouseandroidtest.data.remote.login.LOGIN_PATH
import nz.co.warehouseandroidtest.data.remote.login.LoginRemoteDataSource

class UnauthenticatedApiClientTest {

    @Test
    fun login_sendsGuestHeadersAndPath() = runTest {
        var capturedPath = ""
        var capturedAuthorization = ""
        var capturedDevice = ""
        var capturedSubscriptionKey = ""

        val apiClient = mockUnauthenticatedApiClient { request ->
            capturedPath = request.url.encodedPath
            capturedAuthorization = request.headers[HttpHeaders.Authorization].orEmpty()
            capturedDevice = request.headers[HEADER_TWL_DEVICE].orEmpty()
            capturedSubscriptionKey = request.headers[HEADER_SUBSCRIPTION_KEY].orEmpty()
        }

        LoginRemoteDataSource(apiClient).login()

        assertTrue(capturedPath.endsWith("/$LOGIN_PATH"))
        assertEquals("Guest", capturedAuthorization)
        assertEquals("Android", capturedDevice)
        assertEquals("test-subscription-key", capturedSubscriptionKey)
    }

    @Test
    fun login_parsesResponse() = runTest {
        val response = LoginRemoteDataSource(
            mockUnauthenticatedApiClient(LOGIN_RESPONSE_JSON),
        ).login()

        assertEquals("bcbPyICa4tvd4ifoHDRI6IU31B", response.customerId)
        assertEquals(emptyList(), response.preferredBranchIds)
        assertEquals(true, response.guest)
        assertEquals(4.9, response.apiVersion)
        assertEquals(4.6, response.requestedApiVersion)
        assertEquals("2099-09-01T22:29:04Z", response.expiresDatetime)
    }
}
