package nz.co.warehouseandroidtest.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
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

internal fun loginResponseHeaders(token: String = LOGIN_TOKEN): Headers = Headers.build {
    append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
    append(HEADER_TWL_TOKEN, token)
    append(HEADER_TWL_TOKEN_EXPIRES, LOGIN_TOKEN_EXPIRES)
}

internal fun mockUnauthenticatedHttpClient(
    json: String = LOGIN_RESPONSE_JSON,
    onRequest: (HttpRequestData) -> Unit = {},
): HttpClient {
    val engine = MockEngine { request ->
        onRequest(request)
        respond(
            content = json.trimIndent(),
            status = HttpStatusCode.OK,
            headers = loginResponseHeaders(),
        )
    }
    return createWarehouseHttpClient(
        subscriptionKey = "test-subscription-key",
        device = "Android",
        engine = engine,
    )
}

internal fun mockAuthenticatedHttpClient(
    tokenProvider: suspend () -> String? = { LOGIN_TOKEN },
    onRequest: (HttpRequestData) -> Unit = {},
): HttpClient {
    val engine = MockEngine { request ->
        onRequest(request)
        respond(
            content = "{}",
            status = HttpStatusCode.OK,
            headers = Headers.build {
                append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            },
        )
    }
    return createWarehouseHttpClient(
        subscriptionKey = "test-subscription-key",
        device = "Android",
        engine = engine,
    ).installTwlTokenInterceptor(tokenProvider)
}
