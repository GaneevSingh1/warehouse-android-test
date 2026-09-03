package nz.co.warehouseandroidtest.ui.dashboard

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import nz.co.warehouseandroidtest.domain.search.Product
import nz.co.warehouseandroidtest.ui.common.FeaturedProductCard
import nz.co.warehouseandroidtest.ui.theme.AppDimensions
import nz.co.warehouseandroidtest.ui.theme.WarehouseTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import warehousekmpapp.shared.generated.resources.Res
import warehousekmpapp.shared.generated.resources.clear_search
import warehousekmpapp.shared.generated.resources.dashboard_title
import warehousekmpapp.shared.generated.resources.fathers_day_heading
import warehousekmpapp.shared.generated.resources.ic_close
import warehousekmpapp.shared.generated.resources.ic_search
import warehousekmpapp.shared.generated.resources.retry_action
import warehousekmpapp.shared.generated.resources.search_action
import warehousekmpapp.shared.generated.resources.search_error_message
import warehousekmpapp.shared.generated.resources.search_error_title
import warehousekmpapp.shared.generated.resources.search_products_placeholder
import warehousekmpapp.shared.generated.resources.welcome_headline
import warehousekmpapp.shared.generated.resources.welcome_subtitle

@Composable
fun DashboardScreen(
    onSearchSubmitted: (String) -> Unit = {},
    onProductClick: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = koinViewModel(),
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var query by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is DashboardEvent.SearchSubmitted -> {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                    onSearchSubmitted(event.query)
                }
            }
        }
    }

    DashboardContent(
        query = query,
        onQueryChange = { query = it },
        onClearQuery = { query = "" },
        onSearch = viewModel::onSearch,
        fathersDayUiState = viewModel.fathersDayUiState,
        onProductClick = onProductClick,
        onRetryFathersDay = viewModel::retryFathersDay,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DashboardContent(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    onSearch: (String) -> Unit,
    fathersDayUiState: FathersDayUiState,
    onProductClick: (String) -> Unit,
    onRetryFathersDay: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.dashboard_title),
                        fontWeight = FontWeight.Bold,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                }
                .verticalScroll(rememberScrollState())
                .padding(AppDimensions.PaddingLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            WelcomeHeader()
            Spacer(modifier = Modifier.height(AppDimensions.PaddingSmall))
            WelcomeIllustration()
            Spacer(modifier = Modifier.height(AppDimensions.PaddingSmall))
            ProductSearchField(
                query = query,
                onQueryChange = onQueryChange,
                onClearQuery = onClearQuery,
                onSearch = { onSearch(query) },
            )
            Spacer(modifier = Modifier.height(AppDimensions.PaddingMedium))
            Button(
                onClick = { onSearch(query) },
                enabled = query.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_search),
                    contentDescription = null,
                    modifier = Modifier.size(ButtonDefaults.IconSize),
                )
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(text = stringResource(Res.string.search_action))
            }
            if (fathersDayUiState != FathersDayUiState.Empty) {
                Spacer(modifier = Modifier.height(AppDimensions.PaddingExtraLarge))
                FathersDaySection(
                    uiState = fathersDayUiState,
                    onProductClick = onProductClick,
                    onRetry = onRetryFathersDay,
                )
            }
        }
    }
}

@Composable
private fun FathersDaySection(
    uiState: FathersDayUiState,
    onProductClick: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = stringResource(Res.string.fathers_day_heading),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(AppDimensions.PaddingMedium))
        when (uiState) {
            FathersDayUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(AppDimensions.FeaturedProductImageSize),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
            FathersDayUiState.Error -> {
                Text(
                    text = stringResource(Res.string.search_error_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(modifier = Modifier.height(AppDimensions.PaddingExtraSmall))
                Text(
                    text = stringResource(Res.string.search_error_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(AppDimensions.PaddingSmall))
                Button(onClick = onRetry) {
                    Text(text = stringResource(Res.string.retry_action))
                }
            }
            is FathersDayUiState.Success -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.PaddingSmall),
                    verticalAlignment = Alignment.Top,
                ) {
                    uiState.products.forEach { product ->
                        FeaturedProductCard(
                            product = product,
                            onClick = { onProductClick(product.id) },
                        )
                    }
                }
            }
            FathersDayUiState.Empty -> Unit
        }
    }
}

@Composable
private fun WelcomeHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.welcome_headline),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(AppDimensions.PaddingSmall))
        Text(
            text = stringResource(Res.string.welcome_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ProductSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(text = stringResource(Res.string.search_products_placeholder)) },
        leadingIcon = {
            Icon(
                painter = painterResource(Res.drawable.ic_search),
                contentDescription = null,
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClearQuery) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_close),
                        contentDescription = stringResource(Res.string.clear_search),
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(AppDimensions.CornerRadiusLarge),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch(query) }),
    )
}

@Preview
@Composable
private fun DashboardScreenPreview() {
    WarehouseTheme {
        DashboardContent(
            query = "",
            onQueryChange = {},
            onClearQuery = {},
            onSearch = {},
            fathersDayUiState = FathersDayUiState.Success(
                products = listOf(
                    Product(
                        id = "R2820075",
                        name = "Living & Co Stacking Stool",
                        brand = "Living & Co",
                        price = 15.0,
                        imageUrl = "https://example.com/stool.jpg",
                    ),
                    Product(
                        id = "R111",
                        name = "Image Group Stool",
                        brand = "House",
                        price = 20.5,
                        imageUrl = "https://example.com/group-only.jpg",
                    ),
                ),
            ),
            onProductClick = {},
            onRetryFathersDay = {},
        )
    }
}
