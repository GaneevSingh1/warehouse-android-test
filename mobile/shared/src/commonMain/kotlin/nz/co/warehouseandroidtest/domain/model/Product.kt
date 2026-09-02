package nz.co.warehouseandroidtest.domain.model

data class Product(
    val id: String,
    val name: String,
    val brand: String?,
    val price: Double?,
    val imageUrl: String?,
)

data class SearchResult(
    val searchTerm: String,
    val total: Int,
    val products: List<Product>,
)
