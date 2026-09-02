package nz.co.warehouseandroidtest.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import warehousekmpapp.shared.generated.resources.Res
import warehousekmpapp.shared.generated.resources.ic_barcode
import warehousekmpapp.shared.generated.resources.ic_package
import warehousekmpapp.shared.generated.resources.ic_search
import warehousekmpapp.shared.generated.resources.ic_shopping_bag

@Composable
internal fun WelcomeIllustration(modifier: Modifier = Modifier) {
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(188.dp)
                .background(
                    color = colorScheme.primary.copy(alpha = 0.08f),
                    shape = CircleShape,
                ),
        )
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = (-4).dp, y = 56.dp)
                .size(120.dp)
                .background(
                    color = colorScheme.primaryContainer.copy(alpha = 0.55f),
                    shape = CircleShape,
                ),
        )

        FloatingGlyph(
            icon = Res.drawable.ic_package,
            modifier = Modifier.offset(x = (-88).dp, y = 28.dp),
            containerColor = colorScheme.secondaryContainer,
            contentColor = colorScheme.onSecondaryContainer,
            rotation = -10f,
            glyphSize = 68.dp,
            iconSize = 30.dp,
        )
        FloatingGlyph(
            icon = Res.drawable.ic_barcode,
            modifier = Modifier.offset(x = 92.dp, y = 36.dp),
            containerColor = colorScheme.tertiaryContainer,
            contentColor = colorScheme.onTertiaryContainer,
            rotation = 12f,
            glyphSize = 60.dp,
            iconSize = 26.dp,
        )
        FloatingGlyph(
            icon = Res.drawable.ic_search,
            modifier = Modifier.offset(x = 72.dp, y = (-52).dp),
            containerColor = colorScheme.primaryContainer,
            contentColor = colorScheme.primary,
            rotation = 8f,
            glyphSize = 56.dp,
            iconSize = 24.dp,
        )

        Box(
            modifier = Modifier
                .size(124.dp)
                .shadow(elevation = 10.dp, shape = CircleShape)
                .background(color = colorScheme.surface, shape = CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_shopping_bag),
                contentDescription = null,
                tint = colorScheme.primary,
                modifier = Modifier.size(58.dp),
            )
        }
    }
}

@Composable
private fun FloatingGlyph(
    icon: DrawableResource,
    containerColor: Color,
    contentColor: Color,
    rotation: Float,
    glyphSize: Dp,
    iconSize: Dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(glyphSize)
            .graphicsLayer { rotationZ = rotation }
            .shadow(elevation = 4.dp, shape = CircleShape)
            .background(color = containerColor, shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(iconSize),
        )
    }
}
