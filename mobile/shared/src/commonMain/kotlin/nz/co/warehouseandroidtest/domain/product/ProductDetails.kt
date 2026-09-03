package nz.co.warehouseandroidtest.domain.product

data class ProductDetails(
    val id: String,
    val name: String,
    val brand: String?,
    val price: Double?,
    val imageUrls: List<String>,
    val description: String?,
    val features: List<String>,
    val colour: String?,
    val barcode: String?,
    val onSpecial: Boolean,
    val isClearance: Boolean,
    val available: Boolean,
    val stockOnHand: Int?,
    val soldOnline: Boolean,
    val clickAndCollect: Boolean,
    val promotions: List<ProductPromotion>,
    val categoryPath: List<String>,
)

data class ProductPromotion(
    val id: String,
    val description: String,
    val extraDetail: String?,
    val isMarketClubExclusive: Boolean,
)
