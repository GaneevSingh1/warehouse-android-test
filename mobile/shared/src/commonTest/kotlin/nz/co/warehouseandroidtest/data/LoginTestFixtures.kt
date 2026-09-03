package nz.co.warehouseandroidtest.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockEngineConfig
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import nz.co.warehouseandroidtest.data.remote.HEADER_TWL_TOKEN
import nz.co.warehouseandroidtest.data.remote.HEADER_TWL_TOKEN_EXPIRES
import nz.co.warehouseandroidtest.data.remote.createWarehouseHttpClient
import nz.co.warehouseandroidtest.data.remote.installTwlTokenInterceptor

internal const val LOGIN_TOKEN = "test-twl-token"
internal const val LOGIN_TOKEN_EXPIRES = "2099-09-01T22:29:04Z"

internal const val LOGIN_RESPONSE_JSON = """
{
    "customerId": "bcbPyICa4tvd4ifoHDRI6IU31B",
    "preferredBranchIds": [],
    "eReceiptsPreferred": false,
    "isTeamMember": false,
    "isStaff": false,
    "masterEmailOptIn": false,
    "expiresDatetime": "2099-09-01T22:29:04Z",
    "expiryMinutes": 29,
    "guest": true,
    "platformDemandWare": "QAT",
    "environment": "Azure QAT",
    "developmentPlatform": true,
    "apiVersion": 4.9,
    "requestedApiVersion": 4.6
}
"""

internal const val EXPIRED_TOKEN_EXPIRES = "2020-01-01T00:00:00Z"

internal fun loginResponseHeaders(
    token: String = LOGIN_TOKEN,
    expiresDatetime: String = LOGIN_TOKEN_EXPIRES,
): Headers = Headers.build {
    append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
    append(HEADER_TWL_TOKEN, token)
    append(HEADER_TWL_TOKEN_EXPIRES, expiresDatetime)
}

internal fun mockUnauthenticatedHttpClient(
    json: String = LOGIN_RESPONSE_JSON,
    status: HttpStatusCode = HttpStatusCode.OK,
    expiresDatetime: String = LOGIN_TOKEN_EXPIRES,
    onRequest: (HttpRequestData) -> Unit = {},
): HttpClient = createWarehouseHttpClient(
    subscriptionKey = "test-subscription-key",
    device = "Android",
    engine = mockEngine(json, status, loginResponseHeaders(expiresDatetime = expiresDatetime), onRequest),
)

internal fun mockAuthenticatedHttpClient(
    json: String = "{}",
    status: HttpStatusCode = HttpStatusCode.OK,
    tokenProvider: suspend () -> String = { LOGIN_TOKEN },
    onUnauthorized: suspend () -> Unit = {},
    onRequest: (HttpRequestData) -> Unit = {},
): HttpClient = createWarehouseHttpClient(
    subscriptionKey = "test-subscription-key",
    device = "Android",
    engine = mockEngine(
        json = json,
        status = status,
        headers = Headers.build {
            append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        },
        onRequest = onRequest,
    ),
).installTwlTokenInterceptor(tokenProvider, onUnauthorized)

private fun mockEngine(
    json: String,
    status: HttpStatusCode,
    headers: Headers,
    onRequest: (HttpRequestData) -> Unit,
): MockEngine = MockEngine(
    MockEngineConfig().apply {
        dispatcher = Dispatchers.Unconfined
        addHandler { request ->
            onRequest(request)
            respond(
                content = json.trimIndent(),
                status = status,
                headers = headers,
            )
        }
    },
)
