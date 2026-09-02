package nz.co.warehouseandroidtest.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.plugin
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.appendPathSegments
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

internal const val API_BASE_URL = "https://legacy-apim.twg.co.nz/twgCSharpTest/"
internal const val HEADER_TWL_DEVICE = "X-TWL-Device"
internal const val HEADER_SUBSCRIPTION_KEY = "Ocp-Apim-Subscription-Key"
internal const val HEADER_TWL_TOKEN = "X-TWL-Token"
internal const val HEADER_TWL_TOKEN_EXPIRES = "X-TWL-Token-Expires"

internal val defaultJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
    explicitNulls = false
    encodeDefaults = true
}

fun interface TwlTokenProvider {
    suspend fun getToken(): String?
}

internal fun createWarehouseHttpClient(
    subscriptionKey: String,
    device: String,
    json: Json = defaultJson,
    engine: HttpClientEngine? = null,
): HttpClient {
    val config: HttpClientConfig<*>.() -> Unit = {
        install(ContentNegotiation) {
            json(json)
        }
        defaultRequest {
            url(API_BASE_URL)
            header(HttpHeaders.Authorization, "Guest")
            header(HEADER_TWL_DEVICE, device)
            header(HEADER_SUBSCRIPTION_KEY, subscriptionKey)
        }
    }
    return if (engine == null) {
        HttpClient(config)
    } else {
        HttpClient(engine, config)
    }
}

internal fun HttpClient.installTwlTokenInterceptor(tokenProvider: TwlTokenProvider): HttpClient {
    plugin(HttpSend).intercept { request ->
        val token = tokenProvider.getToken()
        if (!token.isNullOrBlank()) {
            request.header(HEADER_TWL_TOKEN, token)
        }
        execute(request)
    }
    return this
}

internal suspend fun HttpClient.getRelative(path: String): HttpResponse = get {
    url.appendPathSegments(path.trimStart('/'))
}
