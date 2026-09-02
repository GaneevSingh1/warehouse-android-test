package nz.co.warehouseandroidtest.data.remote

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.plugin
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.json.Json

internal const val HEADER_TWL_DEVICE = "X-TWL-Device"
internal const val HEADER_SUBSCRIPTION_KEY = "Ocp-Apim-Subscription-Key"
internal const val HEADER_TWL_TOKEN = "X-TWL-Token"
internal const val HEADER_TWL_TOKEN_EXPIRES = "X-TWL-Token-Expires"

private val logger = Logger.withTag("WarehouseHttpClient")

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

internal suspend fun <T> HttpClient.getResult(
    url: String,
    parameters: Map<String, String> = emptyMap(),
    parse: suspend (HttpResponse) -> T,
): Result<T> = runApiCatching {
    val response = get(url) {
        parameters.forEach { (name, value) ->
            parameter(name, value)
        }
    }
    if (!response.status.isSuccess()) {
        handleFailure(response)
    }
    parse(response)
}

private suspend fun handleFailure(response: HttpResponse): Nothing {
    val body = runCatching { response.bodyAsText() }.getOrElse { "<unavailable: ${it.message}>" }
    val headers = response.headers.entries().joinToString("\n") { (name, values) ->
        val renderedValue = when (name) {
            HEADER_SUBSCRIPTION_KEY, HEADER_TWL_TOKEN, HEADER_TWL_TOKEN_EXPIRES -> "<redacted>"
            else -> values.joinToString()
        }
        "  $name: $renderedValue"
    }.ifEmpty { "  <none>" }
    val details = buildString {
        appendLine("Request failed with HTTP ${response.status}")
        appendLine("URL: ${response.request.url}")
        appendLine("Headers:")
        appendLine(headers)
        append("Body: $body")
    }
    logger.e { details }
    error(details)
}

private suspend inline fun <T> runApiCatching(block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch (e: CancellationException) {
    throw e
} catch (e: Throwable) {
    Result.failure(e)
}
