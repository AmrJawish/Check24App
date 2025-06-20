package com.example.check24app.ui.overview

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.check24app.model.Product
import com.example.check24app.viewmodel.ProductOverviewViewModel
import com.example.check24app.viewmodel.ProductUiState

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
        Text(text = "Loaded ${products.size} products", style = MaterialTheme.typography.headlineSmall)
        // We'll replace this with LazyColumn and ProductCard later
    }
}
