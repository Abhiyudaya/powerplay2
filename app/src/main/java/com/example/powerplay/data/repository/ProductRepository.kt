package com.example.powerplay.data.repository

import com.example.powerplay.data.model.Product
import com.example.powerplay.data.model.ProductsResponse
import com.example.powerplay.data.network.NetworkResult

interface ProductRepository {
    suspend fun getProducts(page: Int): NetworkResult<ProductsResponse>
    suspend fun getProductById(id: Int): NetworkResult<Product>
}