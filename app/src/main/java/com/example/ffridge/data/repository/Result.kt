package com.example.ffridge.data.repository

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

fun <T> Result<T>.getOrNull(): T? {
    return if (this is Result.Success) {
        data
    } else {
        null
    }
}

fun <T> Result<T>.getOrDefault(default: T): T {
    return if (this is Result.Success) {
        data
    } else {
        default
    }
}

fun <T> Result<T>.isSuccess(): Boolean {
    return this is Result.Success
}

fun <T> Result<T>.isError(): Boolean {
    return this is Result.Error
}

fun <T> Result<T>.isLoading(): Boolean {
    return this is Result.Loading
}
