package com.example.check24app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.check24app.model.Product
import com.example.check24app.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.check24app.model.ProductFilter
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn


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

    fun setFilter(filter: ProductFilter) {
        _currentFilter.value = filter
    }

    val filteredProducts: StateFlow<List<Product>> = combine(
        _uiState,
        _currentFilter
    ) { state, filter ->
        if (state is ProductUiState.Success) {
            val allProducts = state.products
            when (filter) {
                ProductFilter.ALL -> allProducts
                ProductFilter.AVAILABLE -> allProducts.filter { it.available }
                ProductFilter.FAVORITES -> allProducts.filter { it.id % 2 == 0 } // simulate
            }
        } else emptyList()
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

}
