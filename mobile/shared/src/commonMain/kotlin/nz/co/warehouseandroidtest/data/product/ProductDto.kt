package nz.co.warehouseandroidtest.data.product

import kotlinx.serialization.Serializable
import nz.co.warehouseandroidtest.data.search.ImageGroupDto
import nz.co.warehouseandroidtest.data.search.PriceInfoDto

@Serializable
data class ProductResponseDto(
    val product: ProductDetailsDto? = null,
)

@Serializable
data class ProductDetailsDto(
    val productName: String? = null,
    val productKey: Long? = null,
    val productId: String? = null,
    val brandDescription: String? = null,
    val productBarcode: String? = null,
    val productDescription: String? = null,
    val colourDescription: String? = null,
    val onSpecial: Boolean = false,
    val isClearance: Boolean = false,
    val soldOnline: String? = null,
    val isClickAndCollect: Boolean = false,
    val priceInfo: PriceInfoDto? = null,
    val inventory: InventoryDto? = null,
    val imageUrls: List<String> = emptyList(),
    val imageGroups: List<ImageGroupDto> = emptyList(),
    val featureList: List<String> = emptyList(),
    val promotions: List<PromotionDto> = emptyList(),
    val categoryHierarchy: List<CategoryDto> = emptyList(),
)

@Serializable
data class InventoryDto(
    val available: Boolean = false,
    val preorderable: Boolean = false,
    val backorderable: Boolean = false,
    val soh: Int? = null,
)

@Serializable
data class PromotionDto(
    val promotionId: String? = null,
    val dealDescription: String? = null,
    val demandwareConditionsText: String? = null,
    val price: Double? = null,
    val isMarketClubExclusive: Boolean = false,
    val description: String? = null,
    val tags: List<String> = emptyList(),
)

@Serializable
data class CategoryDto(
    val categoryId: String? = null,
    val name: String? = null,
)

internal fun ProductDetailsDto.resolveImageUrls(): List<String> {
    val fromProduct = imageUrls.filter { it.isNotBlank() }
    val fromGroups = imageGroups.flatMap { it.imageUrls }.filter { it.isNotBlank() }
    return (fromProduct + fromGroups).distinct()
}
