package com.example.check24app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.check24app.ui.overview.ProductDetailScreen
import com.example.check24app.ui.overview.ProductOverviewScreen
import com.example.check24app.ui.overview.WebViewScreen
import com.example.check24app.ui.theme.Check24AppTheme
import com.example.check24app.viewmodel.ProductOverviewViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Check24AppTheme {
                val navController = rememberNavController()
                val sharedViewModel: ProductOverviewViewModel = viewModel()

                NavHost(navController = navController, startDestination = "overview") {

                    composable("overview") {
                        ProductOverviewScreen(
                            viewModel = sharedViewModel,
                            onProductClick = { productId -> navController.navigate("detail/$productId") },
                            onFooterClick = { navController.navigate("webview") }
                        )
                    }

                    composable("detail/{productId}") { backStackEntry ->
                        val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull()
                        if (productId != null) {
                            ProductDetailScreen(
                                productId = productId,
                                navController = navController,
                                viewModel = sharedViewModel
                            )
                        }
                    }

                    composable("webview") {
                        WebViewScreen(url = "http://m.check24.de/rechtliche-hinweise?deviceoutput=app")
                    }
                }
            }
        }
    }
}
