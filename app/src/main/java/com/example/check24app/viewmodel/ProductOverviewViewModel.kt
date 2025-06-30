package com.example.check24app.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshotFlow
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

    private var loadAttempt = 0

    private val _activeProduct = MutableStateFlow<Product?>(null)



    init {
        loadProducts()
    }

    fun loadProducts() {
        _uiState.value = ProductUiState.Loading
        viewModelScope.launch {
            loadAttempt++

            val result = if (loadAttempt % 3 == 0) {
                Result.failure(Exception("Simulated error"))
            } else {
                repository.getProducts()
            }

            if (result.isSuccess) {
                val newProducts = result.getOrNull()?.products ?: emptyList()
                val updated = newProducts.map { newProduct ->
                    val existing = _products.find { it.id == newProduct.id }
                    if (existing != null && existing.isFavorite) {
                        newProduct.copy(isFavorite = true)
                    } else {
                        newProduct
                    }
                }

                _products.clear()
                _products.addAll(updated)
                _uiState.value = ProductUiState.Success(updated)
            } else {
                _uiState.value = ProductUiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }



    fun setFilter(filter: ProductFilter) {
        _currentFilter.value = filter
    }


    fun observeProductById(productId: Int): StateFlow<Product?> {
        // Update on first call
        if (_activeProduct.value?.id != productId) {
            _activeProduct.value = _products.find { it.id == productId }
        }
        return _activeProduct
    }





    fun toggleFavorite(productId: Int) {
        val index = _products.indexOfFirst { it.id == productId }
        if (index != -1) {
            val updatedProduct = _products[index].copy(isFavorite = !_products[index].isFavorite)
            _products[index] = updatedProduct

            // update observed product
            if (_activeProduct.value?.id == productId) {
                _activeProduct.value = updatedProduct
            }
        }

        _currentFilter.value = _currentFilter.value
    }


    val filteredProducts: StateFlow<List<Product>> = combine(
        _uiState,
        _currentFilter,
        snapshotFlow { _products.toList() } // convert observable list to flow
    ) { state, filter, currentProducts ->
        if (state is ProductUiState.Success) {
            when (filter) {
                ProductFilter.ALL -> currentProducts
                ProductFilter.AVAILABLE -> currentProducts.filter { it.available }
                ProductFilter.FAVORITES -> currentProducts.filter { it.isFavorite }
            }
        } else emptyList()

    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}
