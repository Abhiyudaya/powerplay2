package com.example.powerplay.ui.productlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.powerplay.data.model.Product
import com.example.powerplay.data.network.NetworkResult
import com.example.powerplay.data.repository.ProductRepository
import com.example.powerplay.ui.state.PaginationState
import com.example.powerplay.ui.state.UiState
import kotlinx.coroutines.launch

class ProductListViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    private val _products = MutableLiveData<List<Product>>(emptyList())
    val products: LiveData<List<Product>> = _products

    private val _uiState = MutableLiveData<UiState<List<Product>>>()
    val uiState: LiveData<UiState<List<Product>>> = _uiState

    private val _paginationState = MutableLiveData(PaginationState())
    val paginationState: LiveData<PaginationState> = _paginationState

    private val _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    init {
        loadProducts()
    }

    fun loadProducts(page: Int = 0) {
        viewModelScope.launch {
            if (page == 0) {
                _uiState.value = UiState.Loading()
            } else {
                _paginationState.value = _paginationState.value?.copy(isLoadingNextPage = true)
            }

            when (val result = repository.getProducts(page)) {
                is NetworkResult.Success -> {
                    val newProducts = result.data.products
                    val currentProducts = if (page == 0) emptyList() else _products.value ?: emptyList()
                    val updatedProducts = currentProducts + newProducts

                    _products.value = updatedProducts
                    _uiState.value = UiState.Success(updatedProducts)
                    
                    _paginationState.value = PaginationState(
                        currentPage = result.data.currentPage,
                        hasNextPage = result.data.nextPage != null,
                        isLoadingNextPage = false,
                        totalPages = result.data.totalPages
                    )
                }
                is NetworkResult.Error -> {
                    if (page == 0) {
                        _uiState.value = UiState.Error(result.message)
                    } else {
                        _paginationState.value = _paginationState.value?.copy(isLoadingNextPage = false)
                        // Show error message for pagination failure
                    }
                }
                is NetworkResult.Exception -> {
                    if (page == 0) {
                        _uiState.value = UiState.Error("Network error occurred")
                    } else {
                        _paginationState.value = _paginationState.value?.copy(isLoadingNextPage = false)
                    }
                }
            }

            _isRefreshing.value = false
        }
    }

    fun loadNextPage() {
        val currentState = _paginationState.value
        if (currentState?.hasNextPage == true && !currentState.isLoadingNextPage) {
            loadProducts(currentState.currentPage + 1)
        }
    }

    fun retry() {
        loadProducts(0)
    }

    fun refresh() {
        _isRefreshing.value = true
        _paginationState.value = PaginationState() // Reset pagination
        loadProducts(0)
    }
}