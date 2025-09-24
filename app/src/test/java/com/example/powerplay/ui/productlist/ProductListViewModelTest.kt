package com.example.powerplay.ui.productlist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.powerplay.data.model.Product
import com.example.powerplay.data.model.ProductsResponse
import com.example.powerplay.data.network.NetworkResult
import com.example.powerplay.data.repository.ProductRepository
import com.example.powerplay.ui.state.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class ProductListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: ProductRepository

    @Mock
    private lateinit var uiStateObserver: Observer<UiState<List<Product>>>

    @Mock
    private lateinit var productsObserver: Observer<List<Product>>

    private lateinit var viewModel: ProductListViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadProducts success should update ui state and products`() = runTest {
        // Given
        val mockProducts = listOf(
            Product(1, "Product 1", "Description 1", "Electronics", 99.99, "image1.jpg"),
            Product(2, "Product 2", "Description 2", "Electronics", 149.99, "image2.jpg")
        )
        val mockResponse = ProductsResponse(
            products = mockProducts,
            nextPage = 1,
            totalPages = 5,
            currentPage = 0,
            total = 50
        )
        
        whenever(repository.getProducts(0)).thenReturn(NetworkResult.Success(mockResponse))

        // When
        viewModel = ProductListViewModel(repository)
        viewModel.uiState.observeForever(uiStateObserver)
        viewModel.products.observeForever(productsObserver)
        
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify(uiStateObserver).onChanged(UiState.Loading())
        verify(uiStateObserver).onChanged(UiState.Success(mockProducts))
        verify(productsObserver).onChanged(mockProducts)
    }

    @Test
    fun `loadProducts error should update ui state with error`() = runTest {
        // Given
        val errorMessage = "Network error"
        whenever(repository.getProducts(0)).thenReturn(NetworkResult.Error(errorMessage))

        // When
        viewModel = ProductListViewModel(repository)
        viewModel.uiState.observeForever(uiStateObserver)
        
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify(uiStateObserver).onChanged(UiState.Loading())
        verify(uiStateObserver).onChanged(UiState.Error(errorMessage))
    }

    @Test
    fun `retry should reload products from page 0`() = runTest {
        // Given
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
        
        whenever(repository.getProducts(0)).thenReturn(NetworkResult.Success(mockResponse))

        viewModel = ProductListViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.retry()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify(repository, org.mockito.kotlin.times(2)).getProducts(0)
    }
}