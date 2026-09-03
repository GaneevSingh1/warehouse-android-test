package nz.co.warehouseandroidtest.data.search

import kotlinx.serialization.Serializable

@Serializable
data class SearchResponseDto(
    val products: List<ProductDto> = emptyList(),
    val searchTerm: String = "",
    val total: Int = 0,
)

@Serializable
data class ProductDto(
    val productName: String? = null,
    val productKey: Long? = null,
    val productId: String? = null,
    val brandDescription: String? = null,
    val productImageUrl: String? = null,
    val priceInfo: PriceInfoDto? = null,
    val imageGroups: List<ImageGroupDto> = emptyList(),
)

@Serializable
data class PriceInfoDto(
    val price: Double? = null,
)

@Serializable
data class ImageGroupDto(
    val imageUrls: List<String> = emptyList(),
)

internal fun ProductDto.resolveImageUrl(): String? {
    productImageUrl?.takeIf { it.isNotBlank() }?.let { return it }
    return imageGroups
        .asSequence()
        .flatMap { it.imageUrls.asSequence() }
        .firstOrNull { it.isNotBlank() }
}
