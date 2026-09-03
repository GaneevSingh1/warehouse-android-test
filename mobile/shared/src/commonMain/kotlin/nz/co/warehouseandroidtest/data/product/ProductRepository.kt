package nz.co.warehouseandroidtest.data.product

import nz.co.warehouseandroidtest.domain.product.ProductDetails
import nz.co.warehouseandroidtest.domain.product.ProductPromotion

class ProductRepository(
    private val remoteDataSource: ProductRemoteDataSource,
) {
    suspend fun getProduct(productId: String): Result<ProductDetails> = remoteDataSource.getProduct(productId).mapCatching { response ->
        response.product?.toProductDetails()
            ?: error("Product not found")
    }
}

internal fun ProductDetailsDto.toProductDetails(): ProductDetails? {
    val name = productName?.takeIf { it.isNotBlank() } ?: return null
    val id = productId?.takeIf { it.isNotBlank() } ?: productKey?.toString() ?: return null
    return ProductDetails(
        id = id,
        name = name,
        brand = brandDescription?.takeIf { it.isNotBlank() },
        price = priceInfo?.price,
        imageUrls = resolveImageUrls(),
        description = productDescription?.let(::decodeHtml)?.takeIf { it.isNotBlank() },
        features = featureList.mapNotNull { it.takeIf(String::isNotBlank) },
        colour = colourDescription?.takeIf { it.isNotBlank() },
        barcode = productBarcode?.takeIf { it.isNotBlank() },
        onSpecial = onSpecial,
        isClearance = isClearance,
        available = inventory?.available == true,
        stockOnHand = inventory?.soh,
        soldOnline = soldOnline?.equals("Y", ignoreCase = true) == true,
        clickAndCollect = isClickAndCollect,
        promotions = promotions.toPromotions(),
        categoryPath = categoryHierarchy
            .mapNotNull { it.name?.takeIf(String::isNotBlank) }
            .reversed(),
    )
}

internal fun List<PromotionDto>.toPromotions(): List<ProductPromotion> = mapNotNull { dto ->
    val text = dto.dealDescription?.takeIf { it.isNotBlank() }
        ?: dto.description?.takeIf { it.isNotBlank() }
        ?: return@mapNotNull null
    ProductPromotion(
        id = dto.promotionId.orEmpty(),
        description = text,
        extraDetail = dto.demandwareConditionsText?.takeIf { it.isNotBlank() },
        isMarketClubExclusive = dto.isMarketClubExclusive,
    )
}.distinctBy { it.description }

internal fun decodeHtml(value: String): String {
    val withBreaks = value
        .replace(Regex("(?i)<br\\s*/?>"), "\n")
        .replace(Regex("(?i)</p>"), "\n")
    return withBreaks
        .replace(Regex("<[^>]+>"), "")
        .replace("&amp;", "&")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&quot;", "\"")
        .replace("&#39;", "'")
        .replace("&apos;", "'")
        .replace("&nbsp;", " ")
        .trim()
}
