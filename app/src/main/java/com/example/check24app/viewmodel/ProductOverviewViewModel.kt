package com.example.check24app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.check24app.model.Product
import com.example.check24app.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ProductUiState {
    object Loading : ProductUiState()
    data class Success(val products: List<Product>) : ProductUiState()
    data class Error(val message: String) : ProductUiState()
}

class ProductOverviewViewModel : ViewModel() {

    private val repository = ProductRepository()

    private val _uiState = MutableStateFlow<ProductUiState>(ProductUiState.Loading)
    val uiState: StateFlow<ProductUiState> = _uiState

    init {
        loadProducts()
    }

    fun loadProducts() {
        _uiState.value = ProductUiState.Loading
        viewModelScope.launch {
            val result = repository.getProducts()
            if (result.isSuccess) {
                val products = result.getOrNull()?.products ?: emptyList()
                _uiState.value = ProductUiState.Success(products)
            } else {
                _uiState.value = ProductUiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }
}
