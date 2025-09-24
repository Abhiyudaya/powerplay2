package com.example.powerplay.data.model

import com.google.gson.annotations.SerializedName

data class ProductsResponse(
    @SerializedName("data")
    val products: List<Product>,
    
    @SerializedName("next_page")
    val nextPage: Int?,
    
    @SerializedName("total_pages")
    val totalPages: Int,
    
    @SerializedName("current_page")
    val currentPage: Int,
    
    @SerializedName("total")
    val total: Int
)