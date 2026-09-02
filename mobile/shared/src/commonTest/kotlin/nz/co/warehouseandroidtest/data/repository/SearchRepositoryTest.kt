package nz.co.warehouseandroidtest.data.repository

import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import nz.co.warehouseandroidtest.data.EMPTY_SEARCH_RESPONSE_JSON
import nz.co.warehouseandroidtest.data.SEARCH_RESPONSE_JSON
import nz.co.warehouseandroidtest.data.mockAuthenticatedHttpClient
import nz.co.warehouseandroidtest.data.remote.search.ImageGroupDto
import nz.co.warehouseandroidtest.data.remote.search.PriceInfoDto
import nz.co.warehouseandroidtest.data.remote.search.ProductDto
import nz.co.warehouseandroidtest.data.remote.search.SearchRemoteDataSource

class SearchRepositoryTest {

    @Test
    fun search_mapsProductsAndPrefersProductImageUrl() = runTest {
        val result = SearchRepository(
            SearchRemoteDataSource(mockAuthenticatedHttpClient(SEARCH_RESPONSE_JSON)),
        ).search("stool").getOrThrow()

        assertEquals("stool", result.searchTerm)
        assertEquals(64, result.total)
        assertEquals(3, result.products.size)

        val first = result.products[0]
        assertEquals("R2820075", first.id)
        assertEquals("Living & Co Stacking Stool", first.name)
        assertEquals("Living & Co", first.brand)
        assertEquals(15.0, first.price)
        assertEquals("https://example.com/stool.jpg", first.imageUrl)

        assertEquals("https://example.com/group-only.jpg", result.products[1].imageUrl)
        assertNull(result.products[2].imageUrl)
    }

    @Test
    fun search_returnsEmptyProductList() = runTest {
        val result = SearchRepository(
            SearchRemoteDataSource(mockAuthenticatedHttpClient(EMPTY_SEARCH_RESPONSE_JSON)),
        ).search("xyz").getOrThrow()

        assertEquals("xyz", result.searchTerm)
        assertEquals(0, result.total)
        assertTrue(result.products.isEmpty())
    }

    @Test
    fun search_propagatesRemoteFailure() = runTest {
        val result = SearchRepository(
            SearchRemoteDataSource(
                mockAuthenticatedHttpClient(status = HttpStatusCode.InternalServerError),
            ),
        ).search("stool")

        assertTrue(result.isFailure)
    }

    @Test
    fun toProduct_skipsItemsWithoutName() {
        val dto = ProductDto(
            productName = "  ",
            productId = "R1",
            priceInfo = PriceInfoDto(price = 1.0),
        )

        assertNull(dto.toProduct())
    }

    @Test
    fun toProduct_fallsBackToImageGroups() {
        val dto = ProductDto(
            productName = "Stool",
            productId = "R1",
            imageGroups = listOf(ImageGroupDto(imageUrls = listOf("https://example.com/a.jpg"))),
        )

        assertEquals("https://example.com/a.jpg", dto.toProduct()?.imageUrl)
    }
}
