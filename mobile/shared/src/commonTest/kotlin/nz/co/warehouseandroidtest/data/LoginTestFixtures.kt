package nz.co.warehouseandroidtest.data

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import nz.co.warehouseandroidtest.data.remote.UnauthenticatedApiClient

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

internal fun mockUnauthenticatedApiClient(
    json: String = LOGIN_RESPONSE_JSON,
    onRequest: (HttpRequestData) -> Unit = {},
): UnauthenticatedApiClient {
    val engine = MockEngine { request ->
        onRequest(request)
        respond(
            content = json.trimIndent(),
            status = HttpStatusCode.OK,
            headers = headersOf(HttpHeaders.ContentType, "application/json"),
        )
    }
    return UnauthenticatedApiClient.create(
        subscriptionKey = "test-subscription-key",
        device = "Android",
        engine = engine,
    )
}
