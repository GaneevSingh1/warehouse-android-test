package nz.co.warehouseandroidtest.data.remote

import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest
import nz.co.warehouseandroidtest.data.LOGIN_TOKEN
import nz.co.warehouseandroidtest.data.mockAuthenticatedHttpClient

class AuthenticatedHttpClientTest {

    @Test
    fun get_sendsStoredTwlToken() = runTest {
        var capturedToken = ""
        var capturedAuthorization = ""
        var capturedDevice = ""
        var capturedSubscriptionKey = ""

        val httpClient = mockAuthenticatedHttpClient(
            tokenProvider = { LOGIN_TOKEN },
        ) { request ->
            capturedToken = request.headers[HEADER_TWL_TOKEN].orEmpty()
            capturedAuthorization = request.headers[HttpHeaders.Authorization].orEmpty()
            capturedDevice = request.headers[HEADER_TWL_DEVICE].orEmpty()
            capturedSubscriptionKey = request.headers[HEADER_SUBSCRIPTION_KEY].orEmpty()
        }

        httpClient.get("https://legacy-apim.twg.co.nz/twgCSharpTest/Search.json")

        assertEquals(LOGIN_TOKEN, capturedToken)
        assertEquals("Guest", capturedAuthorization)
        assertEquals("Android", capturedDevice)
        assertEquals("test-subscription-key", capturedSubscriptionKey)
    }
}
