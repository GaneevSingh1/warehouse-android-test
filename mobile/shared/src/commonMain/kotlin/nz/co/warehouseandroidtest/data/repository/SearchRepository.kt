package nz.co.warehouseandroidtest.data.repository

import nz.co.warehouseandroidtest.data.remote.search.DEFAULT_SEARCH_LIMIT
import nz.co.warehouseandroidtest.data.remote.search.DEFAULT_SEARCH_START
import nz.co.warehouseandroidtest.data.remote.search.ProductDto
import nz.co.warehouseandroidtest.data.remote.search.SearchRemoteDataSource
import nz.co.warehouseandroidtest.data.remote.search.resolveImageUrl
import nz.co.warehouseandroidtest.domain.model.Product
import nz.co.warehouseandroidtest.domain.model.SearchResult

class SearchRepository(
    private val remoteDataSource: SearchRemoteDataSource,
) {
    suspend fun search(
        query: String,
        start: Int = DEFAULT_SEARCH_START,
        limit: Int = DEFAULT_SEARCH_LIMIT,
    ): Result<SearchResult> = remoteDataSource.search(query, start, limit).map { response ->
        SearchResult(
            searchTerm = response.searchTerm.ifBlank { query },
            total = response.total,
            products = response.products.mapNotNull { it.toProduct() },
        )
    }
}

internal fun ProductDto.toProduct(): Product? {
    val name = productName?.takeIf { it.isNotBlank() } ?: return null
    val id = productId?.takeIf { it.isNotBlank() } ?: productKey?.toString() ?: return null
    return Product(
        id = id,
        name = name,
        brand = brandDescription?.takeIf { it.isNotBlank() },
        price = priceInfo?.price,
        imageUrl = resolveImageUrl(),
    )
}
