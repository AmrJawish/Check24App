package com.example.check24app.network

import com.example.check24app.model.ProductResponse
import retrofit2.http.GET

interface ProductApiService {

    @GET("products-test.json")
    suspend fun getProducts(): ProductResponse
}