package com.example.powerplay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.powerplay.data.network.ApiClient
import com.example.powerplay.data.repository.ProductRepository
import com.example.powerplay.data.repository.ProductRepositoryImpl
import com.example.powerplay.ui.productlist.ProductListViewModel

class ViewModelFactory : ViewModelProvider.Factory {
    
    private val repository: ProductRepository by lazy {
        ProductRepositoryImpl(ApiClient.productApiService)
    }
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ProductListViewModel::class.java) -> {
                ProductListViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}