package nz.co.warehouseandroidtest.data.product

import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import nz.co.warehouseandroidtest.data.EMPTY_PRODUCT_RESPONSE_JSON
import nz.co.warehouseandroidtest.data.PRODUCT_RESPONSE_JSON
import nz.co.warehouseandroidtest.data.mockAuthenticatedHttpClient
import nz.co.warehouseandroidtest.data.search.ImageGroupDto

class ProductRepositoryTest {

    @Test
    fun getProduct_mapsDetailsAndDecodesDescription() = runTest {
        val result = ProductRepository(
            ProductRemoteDataSource(mockAuthenticatedHttpClient(PRODUCT_RESPONSE_JSON)),
        ).getProduct("R2820075").getOrThrow()

        assertEquals("R2820075", result.id)
        assertEquals("Living & Co Stacking Stool", result.name)
        assertEquals("Living & Co", result.brand)
        assertEquals(15.0, result.price)
        assertEquals(
            listOf("https://example.com/stool.jpg", "https://example.com/stool-hi-res.jpg"),
            result.imageUrls,
        )
        assertEquals(
            "The Living & Co Stacking Stool is an ideal choice for your living room.",
            result.description,
        )
        assertEquals(
            listOf(
                "Powder coated metal legs",
                "Stackable for easy storage",
                "Maximum weight limit: 100kg",
            ),
            result.features,
        )
        assertEquals("White", result.colour)
        assertEquals("9401073417602", result.barcode)
        assertFalse(result.onSpecial)
        assertFalse(result.isClearance)
        assertTrue(result.available)
        assertEquals(70, result.stockOnHand)
        assertTrue(result.soldOnline)
        assertTrue(result.clickAndCollect)
        assertEquals(5, result.promotions.size)
        assertEquals(
            "5% off your order. Download our app and join MarketClub. This is the callout",
            result.promotions.first().description,
        )
        assertEquals("This is the extra detail", result.promotions.first().extraDetail)
        assertEquals(
            listOf(
                "Home, Garden & Appliances",
                "Furniture",
                "Dining Tables & Chairs",
                "Bar Stools",
            ),
            result.categoryPath,
        )
    }

    @Test
    fun getProduct_returnsFailureWhenProductMissing() = runTest {
        val result = ProductRepository(
            ProductRemoteDataSource(mockAuthenticatedHttpClient(EMPTY_PRODUCT_RESPONSE_JSON)),
        ).getProduct("R2820075")

        assertTrue(result.isFailure)
        assertEquals("Product not found", result.exceptionOrNull()?.message)
    }

    @Test
    fun getProduct_propagatesRemoteFailure() = runTest {
        val result = ProductRepository(
            ProductRemoteDataSource(
                mockAuthenticatedHttpClient(status = HttpStatusCode.InternalServerError),
            ),
        ).getProduct("R2820075")

        assertTrue(result.isFailure)
    }

    @Test
    fun toProductDetails_skipsItemsWithoutName() {
        val dto = ProductDetailsDto(
            productName = "  ",
            productId = "R1",
        )

        assertNull(dto.toProductDetails())
    }

    @Test
    fun toProductDetails_fallsBackToImageGroups() {
        val dto = ProductDetailsDto(
            productName = "Stool",
            productId = "R1",
            imageGroups = listOf(ImageGroupDto(imageUrls = listOf("https://example.com/a.jpg"))),
        )

        assertEquals(listOf("https://example.com/a.jpg"), dto.toProductDetails()?.imageUrls)
    }

    @Test
    fun toPromotions_usesDealDescriptionAndDedupes() {
        val promotions = listOf(
            PromotionDto(
                promotionId = "one",
                dealDescription = "Get $8 off",
                description = "Ignored",
            ),
            PromotionDto(
                promotionId = "two",
                dealDescription = "Get $8 off",
            ),
            PromotionDto(
                promotionId = "three",
                description = "Online only",
            ),
            PromotionDto(
                promotionId = "blank",
                dealDescription = "  ",
            ),
        ).toPromotions()

        assertEquals(2, promotions.size)
        assertEquals("Get $8 off", promotions[0].description)
        assertEquals("Online only", promotions[1].description)
    }

    @Test
    fun decodeHtml_unescapesEntitiesAndStripsTags() {
        assertEquals(
            "Living & Co\nStacking Stool",
            decodeHtml("Living &amp; Co<br/>Stacking Stool"),
        )
        assertEquals("Quote \"test\"", decodeHtml("Quote &quot;test&quot;"))
    }
}
