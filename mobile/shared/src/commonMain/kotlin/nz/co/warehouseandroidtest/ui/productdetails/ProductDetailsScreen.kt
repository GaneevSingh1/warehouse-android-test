package nz.co.warehouseandroidtest.ui.productdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import nz.co.warehouseandroidtest.domain.product.ProductDetails
import nz.co.warehouseandroidtest.domain.product.ProductPromotion
import nz.co.warehouseandroidtest.ui.common.formatPrice
import nz.co.warehouseandroidtest.ui.theme.AppDimensions
import nz.co.warehouseandroidtest.ui.theme.WarehouseTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import warehousekmpapp.shared.generated.resources.Res
import warehousekmpapp.shared.generated.resources.ic_arrow_back
import warehousekmpapp.shared.generated.resources.ic_barcode
import warehousekmpapp.shared.generated.resources.ic_package
import warehousekmpapp.shared.generated.resources.ic_shopping_bag
import warehousekmpapp.shared.generated.resources.navigate_back
import warehousekmpapp.shared.generated.resources.product_barcode_label
import warehousekmpapp.shared.generated.resources.product_clearance
import warehousekmpapp.shared.generated.resources.product_click_and_collect
import warehousekmpapp.shared.generated.resources.product_colour_label
import warehousekmpapp.shared.generated.resources.product_description_heading
import warehousekmpapp.shared.generated.resources.product_details_error_message
import warehousekmpapp.shared.generated.resources.product_details_error_title
import warehousekmpapp.shared.generated.resources.product_details_title
import warehousekmpapp.shared.generated.resources.product_features_heading
import warehousekmpapp.shared.generated.resources.product_id_label
import warehousekmpapp.shared.generated.resources.product_image
import warehousekmpapp.shared.generated.resources.product_in_stock
import warehousekmpapp.shared.generated.resources.product_market_club
import warehousekmpapp.shared.generated.resources.product_offers_heading
import warehousekmpapp.shared.generated.resources.product_on_special
import warehousekmpapp.shared.generated.resources.product_out_of_stock
import warehousekmpapp.shared.generated.resources.product_sold_online
import warehousekmpapp.shared.generated.resources.product_stock_count
import warehousekmpapp.shared.generated.resources.retry_action

@Composable
fun ProductDetailsScreen(
    productId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProductDetailsViewModel = koinViewModel(key = productId) { parametersOf(productId) },
) {
    ProductDetailsContent(
        uiState = viewModel.uiState,
        onBack = onBack,
        onRetry = viewModel::retry,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProductDetailsContent(
    uiState: ProductDetailsUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val title = (uiState as? ProductDetailsUiState.Success)?.details?.name
        ?: stringResource(Res.string.product_details_title)
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
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
            ProductDetailsUiState.Loading -> LoadingState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )
            is ProductDetailsUiState.Error -> ErrorState(
                message = uiState.message,
                onRetry = onRetry,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )
            is ProductDetailsUiState.Success -> ProductDetailsBody(
                details = uiState.details,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )
        }
    }
}

@Composable
private fun ProductDetailsBody(
    details: ProductDetails,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
    ) {
        ProductImageGallery(
            imageUrls = details.imageUrls,
            productName = details.name,
        )
        Column(
            modifier = Modifier.padding(AppDimensions.PaddingMedium),
        ) {
            details.brand?.let { brand ->
                Text(
                    text = brand,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(AppDimensions.PaddingExtraSmall))
            }
            Text(
                text = details.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            details.colour?.let { colour ->
                Spacer(modifier = Modifier.height(AppDimensions.PaddingExtraSmall))
                Text(
                    text = stringResource(Res.string.product_colour_label, colour),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            details.price?.let { price ->
                Spacer(modifier = Modifier.height(AppDimensions.PaddingSmall))
                Text(
                    text = formatPrice(price),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            if (details.onSpecial || details.isClearance) {
                Spacer(modifier = Modifier.height(AppDimensions.PaddingSmall))
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimensions.PaddingSmall)) {
                    if (details.onSpecial) {
                        ProductBadge(text = stringResource(Res.string.product_on_special))
                    }
                    if (details.isClearance) {
                        ProductBadge(text = stringResource(Res.string.product_clearance))
                    }
                }
            }
            Spacer(modifier = Modifier.height(AppDimensions.PaddingMedium))
            AvailabilitySection(details = details)
            if (details.categoryPath.isNotEmpty()) {
                Spacer(modifier = Modifier.height(AppDimensions.PaddingMedium))
                Text(
                    text = details.categoryPath.joinToString(" > "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            details.description?.let { description ->
                SectionDivider()
                SectionHeading(text = stringResource(Res.string.product_description_heading))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
            if (details.features.isNotEmpty()) {
                SectionDivider()
                SectionHeading(text = stringResource(Res.string.product_features_heading))
                details.features.forEach { feature ->
                    Text(
                        text = "• $feature",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = AppDimensions.PaddingExtraSmall),
                    )
                }
            }
            if (details.promotions.isNotEmpty()) {
                SectionDivider()
                SectionHeading(text = stringResource(Res.string.product_offers_heading))
                Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.PaddingSmall)) {
                    details.promotions.forEach { promotion ->
                        PromotionCard(promotion = promotion)
                    }
                }
            }
            SectionDivider()
            ProductIdentifiers(details = details)
        }
    }
}

@Composable
private fun ProductImageGallery(
    imageUrls: List<String>,
    productName: String,
    modifier: Modifier = Modifier,
) {
    if (imageUrls.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(AppDimensions.ProductHeroImageHeight)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            ProductImagePlaceholder()
        }
        return
    }
    val pagerState = rememberPagerState(pageCount = { imageUrls.size })
    val placeholder = painterResource(Res.drawable.ic_package)
    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(AppDimensions.ProductHeroImageHeight)
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) { page ->
            AsyncImage(
                model = imageUrls[page],
                contentDescription = stringResource(Res.string.product_image, productName),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
                placeholder = placeholder,
                error = placeholder,
            )
        }
        if (imageUrls.size > 1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AppDimensions.PaddingSmall),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(imageUrls.size) { index ->
                    val selected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(horizontal = AppDimensions.PagerDotSpacing)
                            .size(
                                if (selected) {
                                    AppDimensions.PagerDotSelectedSize
                                } else {
                                    AppDimensions.PagerDotSize
                                },
                            )
                            .clip(CircleShape)
                            .background(
                                if (selected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.outline
                                },
                            ),
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductImagePlaceholder(modifier: Modifier = Modifier) {
    Icon(
        painter = painterResource(Res.drawable.ic_package),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.size(AppDimensions.EmptyStateIconSize),
    )
}

@Composable
private fun ProductBadge(
    text: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        shape = RoundedCornerShape(AppDimensions.CornerRadiusSmall),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(
                horizontal = AppDimensions.PaddingSmall,
                vertical = AppDimensions.PaddingExtraSmall,
            ),
        )
    }
}

@Composable
private fun AvailabilitySection(
    details: ProductDetails,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = AppDimensions.CardElevation),
        shape = RoundedCornerShape(AppDimensions.CornerRadiusMedium),
    ) {
        Column(modifier = Modifier.padding(AppDimensions.PaddingMedium)) {
            val stockLabel = if (details.available) {
                stringResource(Res.string.product_in_stock)
            } else {
                stringResource(Res.string.product_out_of_stock)
            }
            AvailabilityRow(
                icon = Res.drawable.ic_package,
                text = if (details.available && details.stockOnHand != null) {
                    "$stockLabel · ${stringResource(Res.string.product_stock_count, details.stockOnHand)}"
                } else {
                    stockLabel
                },
            )
            if (details.soldOnline) {
                Spacer(modifier = Modifier.height(AppDimensions.PaddingSmall))
                AvailabilityRow(
                    icon = Res.drawable.ic_shopping_bag,
                    text = stringResource(Res.string.product_sold_online),
                )
            }
            if (details.clickAndCollect) {
                Spacer(modifier = Modifier.height(AppDimensions.PaddingSmall))
                AvailabilityRow(
                    icon = Res.drawable.ic_barcode,
                    text = stringResource(Res.string.product_click_and_collect),
                )
            }
        }
    }
}

@Composable
private fun AvailabilityRow(
    icon: DrawableResource,
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(AppDimensions.PlaceholderIconSize),
        )
        Spacer(modifier = Modifier.width(AppDimensions.PaddingSmall))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun PromotionCard(
    promotion: ProductPromotion,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = AppDimensions.CardElevation),
        shape = RoundedCornerShape(AppDimensions.CornerRadiusMedium),
    ) {
        Column(modifier = Modifier.padding(AppDimensions.PaddingMedium)) {
            if (promotion.isMarketClubExclusive) {
                ProductBadge(text = stringResource(Res.string.product_market_club))
                Spacer(modifier = Modifier.height(AppDimensions.PaddingSmall))
            }
            Text(
                text = promotion.description,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            promotion.extraDetail?.let { extraDetail ->
                Spacer(modifier = Modifier.height(AppDimensions.PaddingExtraSmall))
                Text(
                    text = extraDetail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun ProductIdentifiers(
    details: ProductDetails,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(Res.string.product_id_label, details.id),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        details.barcode?.let { barcode ->
            Spacer(modifier = Modifier.height(AppDimensions.PaddingExtraSmall))
            Text(
                text = stringResource(Res.string.product_barcode_label, barcode),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SectionHeading(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier.padding(bottom = AppDimensions.PaddingSmall),
    )
}

@Composable
private fun SectionDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier.padding(vertical = AppDimensions.PaddingMedium),
        color = MaterialTheme.colorScheme.outlineVariant,
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
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(AppDimensions.PaddingLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(Res.string.product_details_error_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(AppDimensions.PaddingSmall))
        Text(
            text = message.ifBlank { stringResource(Res.string.product_details_error_message) },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(AppDimensions.PaddingMedium))
        Button(onClick = onRetry) {
            Text(text = stringResource(Res.string.retry_action))
        }
    }
}

private val previewDetails = ProductDetails(
    id = "R2820075",
    name = "Living & Co Stacking Stool",
    brand = "Living & Co",
    price = 15.0,
    imageUrls = listOf("https://example.com/stool.jpg"),
    description = "The Living & Co Stacking Stool is an ideal choice for your living room, gaming lounge or bedroom.",
    features = listOf(
        "Powder coated metal legs",
        "Stackable for easy storage",
        "Maximum weight limit: 100kg",
    ),
    colour = "White",
    barcode = "9401073417602",
    onSpecial = false,
    isClearance = false,
    available = true,
    stockOnHand = 70,
    soldOnline = true,
    clickAndCollect = true,
    promotions = listOf(
        ProductPromotion(
            id = "marketclub-sandbox-5off",
            description = "5% off your order. Download our app and join MarketClub. This is the callout",
            extraDetail = "This is the extra detail",
            isMarketClubExclusive = false,
        ),
        ProductPromotion(
            id = "twl-\$1app-delivery",
            description = "\$1 App Delivery on Standard Sized Products",
            extraDetail = "Excludes OS Products",
            isMarketClubExclusive = false,
        ),
    ),
    categoryPath = listOf(
        "Home, Garden & Appliances",
        "Furniture",
        "Dining Tables & Chairs",
        "Bar Stools",
    ),
)

@Preview
@Composable
private fun ProductDetailsSuccessPreview() {
    WarehouseTheme {
        ProductDetailsContent(
            uiState = ProductDetailsUiState.Success(previewDetails),
            onBack = {},
            onRetry = {},
        )
    }
}

@Preview
@Composable
private fun ProductDetailsLoadingPreview() {
    WarehouseTheme {
        ProductDetailsContent(
            uiState = ProductDetailsUiState.Loading,
            onBack = {},
            onRetry = {},
        )
    }
}

@Preview
@Composable
private fun ProductDetailsErrorPreview() {
    WarehouseTheme {
        ProductDetailsContent(
            uiState = ProductDetailsUiState.Error("Couldn't load product"),
            onBack = {},
            onRetry = {},
        )
    }
}
