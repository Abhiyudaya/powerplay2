package com.example.powerplay.data.model

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("error")
    val error: String?,
    
    @SerializedName("message")
    val message: String?,
    
    @SerializedName("code")
    val code: Int?
)