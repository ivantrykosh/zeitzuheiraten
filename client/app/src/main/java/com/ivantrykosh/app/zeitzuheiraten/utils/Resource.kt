package com.ivantrykosh.app.zeitzuheiraten.utils

sealed class Resource<T> {
    class Success<T>(val data: T? = null) : Resource<T>()
    class Error<T>(val message: String) : Resource<T>()
    class Loading<T> : Resource<T>()
}