package com.ivantrykosh.app.zeitzuheiraten.utils

sealed class Resource<T> {
    class Success<T>(val data: T? = null) : Resource<T>()
    class Error<T>(val error: Exception) : Resource<T>()
    class Loading<T> : Resource<T>()
}