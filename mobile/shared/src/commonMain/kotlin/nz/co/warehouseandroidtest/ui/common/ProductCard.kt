package nz.co.warehouseandroidtest.ui.common

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import coil3.compose.SubcomposeAsyncImage
import nz.co.warehouseandroidtest.domain.search.Product
import nz.co.warehouseandroidtest.ui.theme.AppDimensions
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import warehousekmpapp.shared.generated.resources.Res
import warehousekmpapp.shared.generated.resources.ic_package
import warehousekmpapp.shared.generated.resources.product_image

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProductCard(
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
                modifier = Modifier.size(AppDimensions.ProductImageSize),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FeaturedProductCard(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier.width(AppDimensions.FeaturedProductCardWidth),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = AppDimensions.CardElevation),
        shape = RoundedCornerShape(AppDimensions.CornerRadiusMedium),
    ) {
        Column {
            ProductImage(
                imageUrl = product.imageUrl,
                productName = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppDimensions.FeaturedProductImageSize),
                shape = RoundedCornerShape(
                    topStart = AppDimensions.CornerRadiusMedium,
                    topEnd = AppDimensions.CornerRadiusMedium,
                ),
            )
            Column(modifier = Modifier.padding(AppDimensions.PaddingSmall)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    minLines = 2,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                product.brand?.let { brand ->
                    Spacer(modifier = Modifier.height(AppDimensions.PaddingExtraSmall))
                    Text(
                        text = brand,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                product.price?.let { price ->
                    Spacer(modifier = Modifier.height(AppDimensions.PaddingExtraSmall))
                    Text(
                        text = formatPrice(price),
                        style = MaterialTheme.typography.titleSmall,
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
    shape: RoundedCornerShape = RoundedCornerShape(AppDimensions.CornerRadiusMedium),
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        if (imageUrl.isNullOrBlank()) {
            ProductImagePlaceholder()
        } else {
            SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = stringResource(Res.string.product_image, productName),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                loading = {
                    CircularProgressIndicator(modifier = Modifier.size(AppDimensions.ImageSpinnerSize))
                },
                error = { ProductImagePlaceholder() },
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
