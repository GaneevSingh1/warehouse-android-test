package nz.co.warehouseandroidtest.data.search

import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import nz.co.warehouseandroidtest.data.SEARCH_RESPONSE_JSON
import nz.co.warehouseandroidtest.data.mockAuthenticatedHttpClient

class SearchRemoteDataSourceTest {

    @Test
    fun search_sendsQueryStartAndLimit() = runTest {
        var capturedUrl = ""

        val httpClient = mockAuthenticatedHttpClient(SEARCH_RESPONSE_JSON) { request ->
            capturedUrl = request.url.toString()
        }

        val result = SearchRemoteDataSource(httpClient).search(
            query = "stool",
            start = 0,
            limit = 20,
        )

        assertTrue(result.isSuccess)
        assertTrue(capturedUrl.startsWith(SEARCH_URL))
        assertTrue(capturedUrl.contains("Search=stool"))
        assertTrue(capturedUrl.contains("Start=0"))
        assertTrue(capturedUrl.contains("Limit=20"))
    }

    @Test
    fun search_parsesProducts() = runTest {
        val response = SearchRemoteDataSource(
            mockAuthenticatedHttpClient(SEARCH_RESPONSE_JSON),
        ).search("stool").getOrThrow()

        assertEquals("stool", response.searchTerm)
        assertEquals(64, response.total)
        assertEquals(3, response.products.size)
        assertEquals("Living & Co Stacking Stool", response.products.first().productName)
        assertEquals("https://example.com/stool.jpg", response.products.first().productImageUrl)
        assertEquals(15.0, response.products.first().priceInfo?.price)
    }

    @Test
    fun search_returnsFailureOnHttpError() = runTest {
        val result = SearchRemoteDataSource(
            mockAuthenticatedHttpClient(status = HttpStatusCode.Unauthorized),
        ).search("stool")

        assertTrue(result.isFailure)
        val message = result.exceptionOrNull()?.message.orEmpty()
        assertTrue(message.contains("401"))
        assertTrue(message.contains("URL:"))
        assertTrue(message.contains("Search=stool"))
    }
}
