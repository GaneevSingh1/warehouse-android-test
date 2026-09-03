package nz.co.warehouseandroidtest.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import nz.co.warehouseandroidtest.domain.search.Product
import nz.co.warehouseandroidtest.domain.search.SearchResult
import nz.co.warehouseandroidtest.ui.common.formatPrice
import nz.co.warehouseandroidtest.ui.theme.AppDimensions
import nz.co.warehouseandroidtest.ui.theme.WarehouseTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import warehousekmpapp.shared.generated.resources.Res
import warehousekmpapp.shared.generated.resources.ic_arrow_back
import warehousekmpapp.shared.generated.resources.ic_arrow_forward
import warehousekmpapp.shared.generated.resources.ic_package
import warehousekmpapp.shared.generated.resources.navigate_back
import warehousekmpapp.shared.generated.resources.next_page
import warehousekmpapp.shared.generated.resources.no_products_found
import warehousekmpapp.shared.generated.resources.no_products_found_message
import warehousekmpapp.shared.generated.resources.previous_page
import warehousekmpapp.shared.generated.resources.product_image
import warehousekmpapp.shared.generated.resources.product_list_title
import warehousekmpapp.shared.generated.resources.product_results_count
import warehousekmpapp.shared.generated.resources.retry_action
import warehousekmpapp.shared.generated.resources.search_error_message
import warehousekmpapp.shared.generated.resources.search_error_title

@Composable
fun ProductListScreen(
    query: String,
    onBack: () -> Unit,
    onProductClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProductListViewModel = koinViewModel(key = query) { parametersOf(query) },
) {
    ProductListContent(
        query = query,
        uiState = viewModel.uiState,
        start = viewModel.start,
        canGoPrevious = viewModel.canGoPrevious,
        canGoNext = viewModel.canGoNext,
        onBack = onBack,
        onProductClick = onProductClick,
        onRetry = viewModel::retry,
        onPreviousPage = viewModel::previousPage,
        onNextPage = viewModel::nextPage,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProductListContent(
    query: String,
    uiState: ProductListUiState,
    start: Int,
    canGoPrevious: Boolean,
    canGoNext: Boolean,
    onBack: () -> Unit,
    onProductClick: (String) -> Unit,
    onRetry: () -> Unit,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = query.ifBlank { stringResource(Res.string.product_list_title) },
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_arrow_back),
                            contentDescription = stringResource(Res.string.navigate_back),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
    ) { innerPadding ->
        when (uiState) {
            ProductListUiState.Loading -> LoadingState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )
            ProductListUiState.Empty -> EmptyState(
                canGoPrevious = canGoPrevious,
                canGoNext = canGoNext,
                onPreviousPage = onPreviousPage,
                onNextPage = onNextPage,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )
            ProductListUiState.Error -> ErrorState(
                onRetry = onRetry,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )
            is ProductListUiState.Success -> ProductList(
                result = uiState.result,
                start = start,
                canGoPrevious = canGoPrevious,
                canGoNext = canGoNext,
                onProductClick = onProductClick,
                onPreviousPage = onPreviousPage,
                onNextPage = onNextPage,
                contentPadding = innerPadding,
            )
        }
    }
}

@Composable
private fun ProductList(
    result: SearchResult,
    start: Int,
    canGoPrevious: Boolean,
    canGoNext: Boolean,
    onProductClick: (String) -> Unit,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = AppDimensions.PaddingMedium,
            top = contentPadding.calculateTopPadding() + AppDimensions.PaddingMedium,
            end = AppDimensions.PaddingMedium,
            bottom = contentPadding.calculateBottomPadding() + AppDimensions.PaddingMedium,
        ),
        verticalArrangement = Arrangement.spacedBy(AppDimensions.PaddingSmall),
    ) {
        item {
            Text(
                text = stringResource(
                    Res.string.product_results_count,
                    start + 1,
                    start + result.products.size,
                    result.total,
                ),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = AppDimensions.PaddingExtraSmall),
            )
        }
        itemsIndexed(
            items = result.products,
            key = { index, product -> "${product.id}-$index" },
        ) { _, product ->
            ProductCard(
                product = product,
                onClick = { onProductClick(product.id) },
            )
        }
        if (canGoPrevious || canGoNext) {
            item(key = "pagination") {
                PaginationBar(
                    canGoPrevious = canGoPrevious,
                    canGoNext = canGoNext,
                    onPreviousPage = onPreviousPage,
                    onNextPage = onNextPage,
                )
            }
        }
    }
}

@Composable
private fun PaginationBar(
    canGoPrevious: Boolean,
    canGoNext: Boolean,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextButton(
            onClick = onPreviousPage,
            enabled = canGoPrevious,
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_back),
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize),
            )
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = stringResource(Res.string.previous_page))
        }
        TextButton(
            onClick = onNextPage,
            enabled = canGoNext,
        ) {
            Text(text = stringResource(Res.string.next_page))
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_forward),
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = AppDimensions.CardElevation),
        shape = RoundedCornerShape(AppDimensions.CornerRadiusMedium),
    ) {
        Row(
            modifier = Modifier.padding(AppDimensions.PaddingSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ProductImage(
                imageUrl = product.imageUrl,
                productName = product.name,
            )
            Spacer(modifier = Modifier.width(AppDimensions.PaddingMedium))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                product.brand?.let { brand ->
                    Spacer(modifier = Modifier.height(AppDimensions.PaddingExtraSmall))
                    Text(
                        text = brand,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                product.price?.let { price ->
                    Spacer(modifier = Modifier.height(AppDimensions.PaddingExtraSmall))
                    Text(
                        text = formatPrice(price),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductImage(
    imageUrl: String?,
    productName: String,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(AppDimensions.CornerRadiusMedium)
    val placeholder = painterResource(Res.drawable.ic_package)
    Box(
        modifier = modifier
            .size(AppDimensions.ProductImageSize)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        if (imageUrl.isNullOrBlank()) {
            ProductImagePlaceholder()
        } else {
            AsyncImage(
                model = imageUrl,
                contentDescription = stringResource(Res.string.product_image, productName),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                placeholder = placeholder,
                error = placeholder,
            )
        }
    }
}

@Composable
private fun ProductImagePlaceholder(modifier: Modifier = Modifier) {
    Icon(
        painter = painterResource(Res.drawable.ic_package),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.size(AppDimensions.PlaceholderIconSize),
    )
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyState(
    canGoPrevious: Boolean,
    canGoNext: Boolean,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(AppDimensions.PaddingLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_package),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(AppDimensions.EmptyStateIconSize),
        )
        Spacer(modifier = Modifier.height(AppDimensions.PaddingMedium))
        Text(
            text = stringResource(Res.string.no_products_found),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(AppDimensions.PaddingSmall))
        Text(
            text = stringResource(Res.string.no_products_found_message),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (canGoPrevious || canGoNext) {
            Spacer(modifier = Modifier.height(AppDimensions.PaddingMedium))
            PaginationBar(
                canGoPrevious = canGoPrevious,
                canGoNext = canGoNext,
                onPreviousPage = onPreviousPage,
                onNextPage = onNextPage,
            )
        }
    }
}

@Composable
private fun ErrorState(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(AppDimensions.PaddingLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(Res.string.search_error_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(AppDimensions.PaddingSmall))
        Text(
            text = stringResource(Res.string.search_error_message),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(AppDimensions.PaddingMedium))
        Button(onClick = onRetry) {
            Text(text = stringResource(Res.string.retry_action))
        }
    }
}

@Preview
@Composable
private fun ProductListSuccessPreview() {
    WarehouseTheme {
        ProductListContent(
            query = "stool",
            uiState = ProductListUiState.Success(
                SearchResult(
                    searchTerm = "stool",
                    total = 64,
                    products = listOf(
                        Product(
                            id = "R2820075",
                            name = "Living & Co Stacking Stool",
                            brand = "Living & Co",
                            price = 15.0,
                            imageUrl = "https://example.com/stool.jpg",
                        ),
                    ),
                ),
            ),
            start = 0,
            canGoPrevious = false,
            canGoNext = true,
            onBack = {},
            onProductClick = {},
            onRetry = {},
            onPreviousPage = {},
            onNextPage = {},
        )
    }
}

@Preview
@Composable
private fun ProductListLoadingPreview() {
    WarehouseTheme {
        ProductListContent(
            query = "stool",
            uiState = ProductListUiState.Loading,
            start = 0,
            canGoPrevious = false,
            canGoNext = false,
            onBack = {},
            onProductClick = {},
            onRetry = {},
            onPreviousPage = {},
            onNextPage = {},
        )
    }
}
