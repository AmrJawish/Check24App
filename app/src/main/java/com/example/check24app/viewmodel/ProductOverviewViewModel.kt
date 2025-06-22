package com.example.check24app.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.check24app.model.Product
import com.example.check24app.repository.ProductRepository
import com.example.check24app.model.ProductFilter
import kotlinx.coroutines.flow.*
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

    private val _currentFilter = MutableStateFlow(ProductFilter.ALL)
    val currentFilter: StateFlow<ProductFilter> = _currentFilter

    private val _products = mutableStateListOf<Product>()

    init {
        loadProducts()
    }

    fun loadProducts() {
        _uiState.value = ProductUiState.Loading
        viewModelScope.launch {
            val result = repository.getProducts()
            if (result.isSuccess) {
                val products = result.getOrNull()?.products ?: emptyList()
                _products.clear()
                _products.addAll(products)
                _uiState.value = ProductUiState.Success(products)
            } else {
                _uiState.value = ProductUiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun setFilter(filter: ProductFilter) {
        _currentFilter.value = filter
    }

    fun toggleFavorite(productId: Int) {
        val index = _products.indexOfFirst { it.id == productId }
        if (index != -1) {
            val updatedProduct = _products[index].copy(isFavorite = !_products[index].isFavorite)
            _products[index] = updatedProduct
        }
    }

    val filteredProducts: StateFlow<List<Product>> = combine(
        _uiState,
        _currentFilter
    ) { state, filter ->
        if (state is ProductUiState.Success) {
            val allProducts = _products
            when (filter) {
                ProductFilter.ALL -> allProducts
                ProductFilter.AVAILABLE -> allProducts.filter { it.available }
                ProductFilter.FAVORITES -> allProducts.filter { it.isFavorite }
            }
        } else emptyList()
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}
