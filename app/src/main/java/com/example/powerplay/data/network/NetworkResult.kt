package com.example.powerplay.data.network

sealed class NetworkResult<T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error<T>(val message: String, val code: Int? = null) : NetworkResult<T>()
    data class Exception<T>(val exception: Throwable) : NetworkResult<T>()
}

inline fun <T> NetworkResult<T>.onSuccess(action: (value: T) -> Unit): NetworkResult<T> {
    if (this is NetworkResult.Success) action(data)
    return this
}

inline fun <T> NetworkResult<T>.onError(action: (message: String, code: Int?) -> Unit): NetworkResult<T> {
    if (this is NetworkResult.Error) action(message, code)
    return this
}

inline fun <T> NetworkResult<T>.onException(action: (exception: Throwable) -> Unit): NetworkResult<T> {
    if (this is NetworkResult.Exception) action(exception)
    return this
}