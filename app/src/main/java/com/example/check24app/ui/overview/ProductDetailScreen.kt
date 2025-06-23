package com.example.check24app.ui.overview

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.check24app.viewmodel.ProductOverviewViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(productId: Int, navController: NavController, viewModel: ProductOverviewViewModel) {
    val product by viewModel.observeProductById(productId).collectAsState()
    val context = LocalContext.current

    product?.let { currentProduct ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Produktdetails") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Zurück")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState()) // ✅ Enables scrolling
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Text(
                    text = currentProduct.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = if (currentProduct.isFavorite)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                AsyncImage(
                    model = currentProduct.imageURL,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = currentProduct.description,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = currentProduct.longDescription,
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    viewModel.toggleFavorite(currentProduct.id)
                }) {
                    Text(if (currentProduct.isFavorite) "Vergessen" else "Vormerken")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "© 2016 Check24",
                    modifier = Modifier.clickable {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://m.check24.de/rechtliche-hinweise?deviceoutput=app")
                        )
                        ContextCompat.startActivity(context, intent, null)
                    }
                )
            }
        }
    } ?: Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Product not found")
    }
}
