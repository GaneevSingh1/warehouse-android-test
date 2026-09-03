package nz.co.warehouseandroidtest.data.search

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import nz.co.warehouseandroidtest.data.getResult
import nz.co.warehouseandroidtest.domain.search.DEFAULT_SEARCH_LIMIT
import nz.co.warehouseandroidtest.domain.search.DEFAULT_SEARCH_START

internal const val SEARCH_URL = "https://legacy-apim.twg.co.nz/twgCSharpTest/Search.json"

class SearchRemoteDataSource(
    private val httpClient: HttpClient,
) {
    suspend fun search(
        query: String,
        start: Int = DEFAULT_SEARCH_START,
        limit: Int = DEFAULT_SEARCH_LIMIT,
    ): Result<SearchResponseDto> = httpClient.getResult(
        url = SEARCH_URL,
        parameters = mapOf(
            "Search" to query,
            "Start" to start.toString(),
            "Limit" to limit.toString(),
        ),
    ) { response ->
        response.body()
    }
}
