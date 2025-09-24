package com.example.powerplay.data.repository

import com.example.powerplay.data.api.ProductApiService
import com.example.powerplay.data.model.Product
import com.example.powerplay.data.model.ProductsResponse
import com.example.powerplay.data.network.NetworkErrorHandler
import com.example.powerplay.data.network.NetworkResult

class ProductRepositoryImpl(
    private val apiService: ProductApiService
) : ProductRepository {
    
    // Simple in-memory cache for products
    private val productCache = mutableMapOf<Int, Product>()
    
    override suspend fun getProducts(page: Int): NetworkResult<ProductsResponse> {
        return try {
            val response = apiService.getProducts(page = page)
            
            if (response.isSuccessful) {
                response.body()?.let { productsResponse ->
                    // Cache products for later retrieval
                    productsResponse.products.forEach { product ->
                        productCache[product.id] = product
                    }
                    NetworkResult.Success(productsResponse)
                } ?: NetworkResult.Error("Empty response body")
            } else {
                NetworkErrorHandler.handleApiError(response)
            }
        } catch (exception: Exception) {
            NetworkErrorHandler.handleException(exception)
        }
    }
    
    override suspend fun getProductById(id: Int): NetworkResult<Product> {
        return try {
            // First check cache
            productCache[id]?.let { cachedProduct ->
                return NetworkResult.Success(cachedProduct)
            }
            
            // If not in cache, we could make an API call here
            // For now, return error since the API doesn't have a single product endpoint
            NetworkResult.Error("Product not found")
        } catch (exception: Exception) {
            NetworkErrorHandler.handleException(exception)
        }
    }
}