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
    val type: String,
    val color: String,
    val imageURL: String,
    val colorCode: String,
    val available: Boolean,
    val releaseDate: Long,
    val description: String,
    val longDescription: String,
    val rating: Double,
    val price: Price
)

data class Price(
    val value: Double,
    val currency: String
)