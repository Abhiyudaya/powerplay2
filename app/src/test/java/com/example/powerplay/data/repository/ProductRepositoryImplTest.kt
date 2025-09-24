package com.example.powerplay.data.repository

import com.example.powerplay.data.api.ProductApiService
import com.example.powerplay.data.model.Product
import com.example.powerplay.data.model.ProductsResponse
import com.example.powerplay.data.network.NetworkResult
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import retrofit2.Response
import java.io.IOException
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProductRepositoryImplTest {

    @Mock
    private lateinit var apiService: ProductApiService

    private lateinit var repository: ProductRepositoryImpl

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = ProductRepositoryImpl(apiService)
    }

    @Test
    fun `getProducts success should return NetworkResult Success`() = runTest {
        // Given
        val mockProducts = listOf(
            Product(1, "Product 1", "Description 1", "Electronics", 99.99, "image1.jpg")
        )
        val mockResponse = ProductsResponse(
            products = mockProducts,
            nextPage = 1,
            totalPages = 5,
            currentPage = 0,
            total = 50
        )
        val response = Response.success(mockResponse)
        whenever(apiService.getProducts(0)).thenReturn(response)

        // When
        val result = repository.getProducts(0)

        // Then
        assertTrue(result is NetworkResult.Success)
        assertEquals(mockResponse, result.data)
    }

    @Test
    fun `getProducts API error should return NetworkResult Error`() = runTest {
        // Given
        val errorResponse = Response.error<ProductsResponse>(404, "Not Found".toResponseBody())
        whenever(apiService.getProducts(0)).thenReturn(errorResponse)

        // When
        val result = repository.getProducts(0)

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals("Not found", result.message)
        assertEquals(404, result.code)
    }

    @Test
    fun `getProducts network exception should return NetworkResult Exception`() = runTest {
        // Given
        whenever(apiService.getProducts(0)).thenThrow(IOException("Network error"))

        // When
        val result = repository.getProducts(0)

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals("Network error occurred", result.message)
    }

    @Test
    fun `getProductById with cached product should return success`() = runTest {
        // Given - First load products to cache them
        val mockProducts = listOf(
            Product(1, "Product 1", "Description 1", "Electronics", 99.99, "image1.jpg")
        )
        val mockResponse = ProductsResponse(
            products = mockProducts,
            nextPage = null,
            totalPages = 1,
            currentPage = 0,
            total = 1
        )
        val response = Response.success(mockResponse)
        whenever(apiService.getProducts(0)).thenReturn(response)
        
        repository.getProducts(0) // Cache the product

        // When
        val result = repository.getProductById(1)

        // Then
        assertTrue(result is NetworkResult.Success)
        assertEquals(mockProducts[0], result.data)
    }

    @Test
    fun `getProductById with non-cached product should return error`() = runTest {
        // When
        val result = repository.getProductById(999)

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals("Product not found", result.message)
    }
}