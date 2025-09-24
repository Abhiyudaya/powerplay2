package com.example.powerplay.ui.state

sealed class UiState<T> {
    class Loading<T> : UiState<T>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error<T>(val message: String) : UiState<T>()
}

data class PaginationState(
    val currentPage: Int = 0,
    val hasNextPage: Boolean = true,
    val isLoadingNextPage: Boolean = false,
    val totalPages: Int = 0
)