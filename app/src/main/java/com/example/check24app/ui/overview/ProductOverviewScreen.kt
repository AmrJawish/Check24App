package com.example.check24app.ui.overview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.check24app.model.Product
import com.example.check24app.model.ProductFilter
import com.example.check24app.viewmodel.ProductOverviewViewModel
import com.example.check24app.viewmodel.ProductUiState
import kotlin.math.floor
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource
import com.example.check24app.R

@Composable
fun ProductOverviewScreen(
    viewModel: ProductOverviewViewModel,
    onProductClick: (Int) -> Unit,
    onFooterClick: () -> Unit
)
 {
    val state by viewModel.uiState.collectAsState()
    val filter by viewModel.currentFilter.collectAsState()
    val products by viewModel.filteredProducts.collectAsState()

    Scaffold(
        bottomBar = {
            if (state !is ProductUiState.Error) {
                FilterBar(
                    selected = filter,
                    onFilterSelected = { viewModel.setFilter(it) }
                )
            }
        }

    ) { innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()) {
            when (state) {
                is ProductUiState.Loading -> LoadingView()
                is ProductUiState.Error -> ErrorView(
                    message = (state as ProductUiState.Error).message,
                    onReload = { viewModel.loadProducts() }
                )
                is ProductUiState.Success -> {
                    SwipeRefresh(
                        state = rememberSwipeRefreshState(isRefreshing = false),
                        onRefresh = { viewModel.loadProducts() }
                    ) {
                        ProductListView(
                            products = products,
                            onToggleFavorite = { viewModel.toggleFavorite(it.toInt()) },
                            onProductClick = onProductClick,
                            onFooterClick = onFooterClick
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun LoadingView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorView(message: String, onReload: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Fehler: $message")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onReload) {
            Text("Neuladen")
        }
    }
}


@Composable
fun ProductListView(
    products: List<Product>,
    onToggleFavorite: (String) -> Unit,
    onProductClick: (Int) -> Unit,
    onFooterClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Title and subtitle at top
        item {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text("Unsere Produkte", style = MaterialTheme.typography.titleLarge)
                Text("Wählen Sie aus unseren besten Angeboten", style = MaterialTheme.typography.bodyMedium)
            }
        }

        // Product cards
        items(products) { product ->
            ProductCard(
                product = product,
                onToggleFavorite = onToggleFavorite,
                onClick = { onProductClick(product.id) }
            )
        }

        // Footer at bottom
        item {
            FooterView(onClick = onFooterClick)
        }
    }
}


@Composable
fun ProductCard(
    product: Product,
    onToggleFavorite: (String) -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (product.isFavorite)
                Color(0xFFD1C4E9) // light purple
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = if (product.available) Arrangement.Start else Arrangement.End
        ) {
            if (product.available) {
                ProductImage(product.imageURL)
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(text = product.name, style = MaterialTheme.typography.titleMedium)
                Text(text = product.description, style = MaterialTheme.typography.bodySmall)
                RatingStars(roundRatingToHalf(product.rating))
            }
            IconButton(onClick = { onToggleFavorite(product.id.toString()) }) {
                Icon(
                    imageVector = if (product.isFavorite) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                    contentDescription = null
                )
            }
            if (!product.available) {
                ProductImage(product.imageURL)
            }
        }
    }
}

@Composable
fun ProductImage(url: String) {
    AsyncImage(
        model = url,
        contentDescription = null,
        modifier = Modifier.size(80.dp),
        onError = { println("Image failed to load: $url") },
        fallback = painterResource(id = R.drawable.ic_broken_image),
        placeholder = painterResource(id = R.drawable.ic_placeholder)
    )
}


fun roundRatingToHalf(rating: Double): Double {
    return floor(rating * 2) / 2
}

@Composable
fun RatingStars(rating: Double) {
    val fullStars = rating.toInt()
    val hasHalfStar = (rating % 1) >= 0.5
    val emptyStars = 5 - fullStars - if (hasHalfStar) 1 else 0

    Row {
        repeat(fullStars) {
            Icon(Icons.Filled.Star, contentDescription = "Full Star")
        }
        if (hasHalfStar) {
            Icon(Icons.Filled.StarHalf, contentDescription = "Half Star")
        }
        repeat(emptyStars) {
            Icon(Icons.Filled.StarBorder, contentDescription = "Empty Star")
        }
    }
}

@Composable
fun FilterBar(
    selected: ProductFilter,
    onFilterSelected: (ProductFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ProductFilter.values().forEach { filter ->
            Button(
                onClick = { onFilterSelected(filter) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (filter == selected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(text = filter.label)
            }
        }
    }
}

@Composable
fun FooterView(onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = "© 2016 Check24")
    }
}
