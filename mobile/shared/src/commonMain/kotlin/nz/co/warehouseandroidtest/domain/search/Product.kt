package nz.co.warehouseandroidtest.domain.search

const val DEFAULT_SEARCH_START = 0
const val DEFAULT_SEARCH_LIMIT = 20

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
