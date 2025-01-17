package com.ivantrykosh.app.zeitzuheiraten.utils

data class State<T>(
    val loading: Boolean = false,
    val error: String = "",
    val data: T? = null,
)
