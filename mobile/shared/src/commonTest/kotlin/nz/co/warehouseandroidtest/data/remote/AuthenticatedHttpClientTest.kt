package nz.co.warehouseandroidtest.data.remote

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockEngineConfig
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import nz.co.warehouseandroidtest.data.LOGIN_TOKEN
import nz.co.warehouseandroidtest.data.LOGIN_TOKEN_EXPIRES
import nz.co.warehouseandroidtest.data.local.AuthLocalDataSource
import nz.co.warehouseandroidtest.data.mockAuthenticatedHttpClient
import nz.co.warehouseandroidtest.data.mockUnauthenticatedHttpClient
import nz.co.warehouseandroidtest.data.remote.login.LoginRemoteDataSource
import nz.co.warehouseandroidtest.data.repository.LoginRepository
import nz.co.warehouseandroidtest.domain.model.LoginSession

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

    @Test
    fun get_doesNotSendRequestWhenTokenProviderFails() = runTest {
        var sent = false
        val httpClient = authenticatedClient(
            tokenProvider = { error("login failed") },
        ) {
            sent = true
            respond("{}", HttpStatusCode.OK, jsonHeaders())
        }

        val result = runCatching {
            httpClient.get("https://legacy-apim.twg.co.nz/twgCSharpTest/Search.json")
        }

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message.orEmpty().contains("login failed"))
        assertFalse(sent)
    }

    @Test
    fun get_retriesOnceWithRefreshedTokenOnUnauthorized() = runTest {
        var token = "stale-token"
        var unauthorizedCallbacks = 0
        val capturedTokens = mutableListOf<String>()
        val httpClient = authenticatedClient(
            tokenProvider = { token },
            onUnauthorized = {
                unauthorizedCallbacks++
                token = "fresh-token"
            },
        ) { request ->
            capturedTokens += request.headers[HEADER_TWL_TOKEN].orEmpty()
            if (capturedTokens.size == 1) {
                respond("denied", HttpStatusCode.Unauthorized)
            } else {
                respond("{}", HttpStatusCode.OK, jsonHeaders())
            }
        }

        val response = httpClient.get("https://legacy-apim.twg.co.nz/twgCSharpTest/Search.json")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(listOf("stale-token", "fresh-token"), capturedTokens)
        assertEquals(1, unauthorizedCallbacks)
    }

    @Test
    fun get_doesNotRetryUnauthorizedMoreThanOnce() = runTest {
        var calls = 0
        var unauthorizedCallbacks = 0
        val httpClient = authenticatedClient(
            tokenProvider = { LOGIN_TOKEN },
            onUnauthorized = { unauthorizedCallbacks++ },
        ) {
            calls++
            respond("denied", HttpStatusCode.Unauthorized)
        }

        val response = httpClient.get("https://legacy-apim.twg.co.nz/twgCSharpTest/Search.json")

        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals(2, calls)
        assertEquals(1, unauthorizedCallbacks)
    }

    @Test
    fun get_relogsInThroughLoginRepositoryAfterUnauthorized() = runTest {
        val localDataSource = AuthLocalDataSource()
        localDataSource.saveLoginSession(
            LoginSession(token = "stale-token", expiresDatetime = LOGIN_TOKEN_EXPIRES),
        )
        val loginRepository = LoginRepository(
            remoteDataSource = LoginRemoteDataSource(mockUnauthenticatedHttpClient()),
            localDataSource = localDataSource,
        )
        val capturedTokens = mutableListOf<String>()
        val httpClient = authenticatedClient(
            tokenProvider = { loginRepository.getToken().getOrThrow() },
            onUnauthorized = loginRepository::invalidateSession,
        ) { request ->
            capturedTokens += request.headers[HEADER_TWL_TOKEN].orEmpty()
            if (capturedTokens.size == 1) {
                respond("denied", HttpStatusCode.Unauthorized)
            } else {
                respond("{}", HttpStatusCode.OK, jsonHeaders())
            }
        }

        val response = httpClient.get("https://legacy-apim.twg.co.nz/twgCSharpTest/Search.json")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(listOf("stale-token", LOGIN_TOKEN), capturedTokens)
        assertEquals(LOGIN_TOKEN, localDataSource.getLoginSession()?.token)
    }

    private fun authenticatedClient(
        tokenProvider: suspend () -> String,
        onUnauthorized: suspend () -> Unit = {},
        handle: MockRequestHandler,
    ) = createWarehouseHttpClient(
        subscriptionKey = "test-subscription-key",
        device = "Android",
        engine = MockEngine(
            MockEngineConfig().apply {
                dispatcher = Dispatchers.Unconfined
                addHandler(handle)
            },
        ),
    ).installTwlTokenInterceptor(tokenProvider, onUnauthorized)

    private fun jsonHeaders(): Headers = Headers.build {
        append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
    }
}
