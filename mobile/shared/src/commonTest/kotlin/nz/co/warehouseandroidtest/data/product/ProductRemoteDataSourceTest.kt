package nz.co.warehouseandroidtest.data.product

import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import nz.co.warehouseandroidtest.data.PRODUCT_RESPONSE_JSON
import nz.co.warehouseandroidtest.data.mockAuthenticatedHttpClient

class ProductRemoteDataSourceTest {

    @Test
    fun getProduct_sendsProductId() = runTest {
        var capturedUrl = ""

        val httpClient = mockAuthenticatedHttpClient(PRODUCT_RESPONSE_JSON) { request ->
            capturedUrl = request.url.toString()
        }

        val result = ProductRemoteDataSource(httpClient).getProduct("R2820075")

        assertTrue(result.isSuccess)
        assertTrue(capturedUrl.startsWith(PRODUCT_URL))
        assertTrue(capturedUrl.contains("ProductId=R2820075"))
    }

    @Test
    fun getProduct_parsesProductDetails() = runTest {
        val response = ProductRemoteDataSource(
            mockAuthenticatedHttpClient(PRODUCT_RESPONSE_JSON),
        ).getProduct("R2820075").getOrThrow()

        val product = response.product
        assertEquals("Living & Co Stacking Stool", product?.productName)
        assertEquals("R2820075", product?.productId)
        assertEquals("Living & Co", product?.brandDescription)
        assertEquals(15.0, product?.priceInfo?.price)
        assertEquals(listOf("https://example.com/stool.jpg"), product?.imageUrls)
        assertEquals(true, product?.inventory?.available)
        assertEquals(70, product?.inventory?.soh)
        assertEquals(6, product?.promotions?.size)
        assertEquals("White", product?.colourDescription)
        assertEquals("Y", product?.soldOnline)
        assertEquals(true, product?.isClickAndCollect)
    }

    @Test
    fun getProduct_returnsFailureOnHttpError() = runTest {
        val result = ProductRemoteDataSource(
            mockAuthenticatedHttpClient(status = HttpStatusCode.Unauthorized),
        ).getProduct("R2820075")

        assertTrue(result.isFailure)
        val message = result.exceptionOrNull()?.message.orEmpty()
        assertTrue(message.contains("401"))
        assertTrue(message.contains("URL:"))
        assertTrue(message.contains("ProductId=R2820075"))
    }
}
