package com.example.check24app.model

data class ProductResponse(
    val header: Header,
    val filters: List<String>,
    val products: List<Product>
)

data class Header(
    val headerTitle: String,
    val headerDescription: String
)

data class Product(
    val id: Int,
    val name: String,
    val imageURL: String,
    val available: Boolean,
    val description: String,
    val longDescription: String,
    val rating: Double,
    var isFavorite: Boolean = false
)
