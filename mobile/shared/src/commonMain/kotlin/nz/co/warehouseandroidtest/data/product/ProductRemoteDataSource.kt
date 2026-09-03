package nz.co.warehouseandroidtest.data.product

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import nz.co.warehouseandroidtest.data.getResult

internal const val PRODUCT_URL = "https://legacy-apim.twg.co.nz/twgCSharpTest/Product.json"

class ProductRemoteDataSource(
    private val httpClient: HttpClient,
) {
    suspend fun getProduct(productId: String): Result<ProductResponseDto> = httpClient.getResult(
        url = PRODUCT_URL,
        parameters = mapOf("ProductId" to productId),
    ) { response ->
        response.body()
    }
}
