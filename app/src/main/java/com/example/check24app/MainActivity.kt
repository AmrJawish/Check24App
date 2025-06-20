package com.example.check24app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.check24app.ui.overview.ProductOverviewScreen
import com.example.check24app.ui.theme.Check24AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Check24AppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    ProductOverviewScreen()
                }
            }
        }
    }
}
