package com.example.powerplay.data.api

import com.example.powerplay.data.model.ProductsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductApiService {
    
    @GET("products")
    suspend fun getProducts(
        @Query("page") page: Int,
        @Query("limit") limit: Int = 10,
        @Query("category") category: String = "electronics"
    ): Response<ProductsResponse>
}