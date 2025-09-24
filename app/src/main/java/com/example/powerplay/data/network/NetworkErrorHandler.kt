package com.example.powerplay.data.network

import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object NetworkErrorHandler {
    
    fun <T> handleApiError(response: Response<T>): NetworkResult.Error<T> {
        return when (response.code()) {
            400 -> NetworkResult.Error("Bad request", 400)
            401 -> NetworkResult.Error("Unauthorized", 401)
            403 -> NetworkResult.Error("Forbidden", 403)
            404 -> NetworkResult.Error("Not found", 404)
            408 -> NetworkResult.Error("Request timeout", 408)
            500 -> NetworkResult.Error("Internal server error", 500)
            502 -> NetworkResult.Error("Bad gateway", 502)
            503 -> NetworkResult.Error("Service unavailable", 503)
            else -> NetworkResult.Error("Unknown error occurred", response.code())
        }
    }
    
    fun <T> handleException(exception: Throwable): NetworkResult<T> {
        return when (exception) {
            is UnknownHostException -> NetworkResult.Error("No internet connection")
            is SocketTimeoutException -> NetworkResult.Error("Request timeout")
            is IOException -> NetworkResult.Error("Network error occurred")
            else -> NetworkResult.Exception(exception)
        }
    }
}