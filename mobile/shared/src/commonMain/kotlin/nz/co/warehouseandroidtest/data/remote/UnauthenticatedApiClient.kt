package nz.co.warehouseandroidtest.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

internal const val API_BASE_URL = "https://legacy-apim.twg.co.nz/twgCSharpTest/"
internal const val HEADER_TWL_DEVICE = "X-TWL-Device"
internal const val HEADER_SUBSCRIPTION_KEY = "Ocp-Apim-Subscription-Key"

internal val defaultJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
    explicitNulls = false
    encodeDefaults = true
}

class UnauthenticatedApiClient(
    internal val httpClient: HttpClient,
) {
    internal suspend inline fun <reified T> get(path: String): T = httpClient.get(path).body()

    companion object {
        fun create(
            subscriptionKey: String = GeneratedApiConfig.SUBSCRIPTION_KEY,
            device: String,
            json: Json = defaultJson,
            engine: HttpClientEngine? = null,
        ): UnauthenticatedApiClient = UnauthenticatedApiClient(
            createUnauthenticatedHttpClient(
                subscriptionKey = subscriptionKey,
                device = device,
                json = json,
                engine = engine,
            ),
        )
    }
}

internal fun createUnauthenticatedHttpClient(
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
