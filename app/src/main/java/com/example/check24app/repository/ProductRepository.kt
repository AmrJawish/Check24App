package com.example.check24app.repository

import com.example.check24app.model.ProductResponse
import com.example.check24app.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductRepository {

    suspend fun getProducts(): Result<ProductResponse> {
        return try {
            val response = withContext(Dispatchers.IO) {
                RetrofitInstance.api.getProducts()
            }
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
