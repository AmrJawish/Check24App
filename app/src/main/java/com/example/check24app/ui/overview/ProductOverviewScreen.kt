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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.check24app.model.Product
import com.example.check24app.model.ProductFilter
import com.example.check24app.viewmodel.ProductOverviewViewModel
import com.example.check24app.viewmodel.ProductUiState
import kotlin.math.floor

@Composable
fun ProductOverviewScreen(
    viewModel: ProductOverviewViewModel = viewModel(),
    onProductClick: (Int) -> Unit
)
 {
    val state by viewModel.uiState.collectAsState()
    val filter by viewModel.currentFilter.collectAsState()
    val products by viewModel.filteredProducts.collectAsState()

    Scaffold(
        bottomBar = {
            FilterBar(
                selected = filter,
                onFilterSelected = { viewModel.setFilter(it) }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()) {
            when (state) {
                is ProductUiState.Loading -> LoadingView()
                is ProductUiState.Error -> ErrorView((state as ProductUiState.Error).message)
                is ProductUiState.Success -> ProductListView(
                    products = products,
                    onToggleFavorite = { viewModel.toggleFavorite(it.toInt()) },
                    onProductClick = onProductClick
                )
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
fun ErrorView(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Error: $message")
    }
}

@Composable
fun ProductListView(
    products: List<Product>,
    onToggleFavorite: (String) -> Unit,
    onProductClick: (Int) -> Unit
)
 {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(products) { product ->
            ProductCard(
                product = product,
                onToggleFavorite = onToggleFavorite,
                onClick = { onProductClick(product.id) }
            )
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
        modifier = Modifier.size(80.dp)
    )
}

fun roundRatingToHalf(rating: Double): Double {
    return floor(rating * 2) / 2
}

@Composable
fun RatingStars(rating: Double) {
    val fullStars = rating.toInt()
    val halfStar = if (rating - fullStars >= 0.5) 1 else 0
    val emptyStars = 5 - fullStars - halfStar

    Row {
        repeat(fullStars) { Text("★") }
        repeat(halfStar) { Text("⯪") } // Optional fallback symbol
        repeat(emptyStars) { Text("☆") }
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
