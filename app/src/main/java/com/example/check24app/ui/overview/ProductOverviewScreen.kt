package com.example.check24app.ui.overview


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.check24app.model.Product
import com.example.check24app.viewmodel.ProductOverviewViewModel
import com.example.check24app.viewmodel.ProductUiState
import coil.compose.AsyncImage



@Composable
fun ProductOverviewScreen(
    viewModel: ProductOverviewViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    when (state) {
        is ProductUiState.Loading -> LoadingView()
        is ProductUiState.Error -> ErrorView((state as ProductUiState.Error).message)
        is ProductUiState.Success -> ProductListView((state as ProductUiState.Success).products)
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
fun ProductListView(products: List<Product>) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(products) { product ->
                ProductCard(product = product)
            }
        }

    }
}

@Composable
fun ProductCard(product: Product) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                Text(text = "Rating: ${product.rating}")
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
        modifier = Modifier
            .size(80.dp)
    )
}

