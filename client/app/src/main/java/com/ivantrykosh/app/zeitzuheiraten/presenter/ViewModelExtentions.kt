package com.ivantrykosh.app.zeitzuheiraten.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import com.ivantrykosh.app.zeitzuheiraten.utils.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

fun <T> ViewModel.clearState(stateFlow: MutableStateFlow<State<T>>) {
    stateFlow.value = State<T>()
}

fun <T> ViewModel.loadPaginatedData(
    reset: Boolean,
    pageSize: Int,
    anyNewItems: (Boolean) -> Unit,
    stateFlow: MutableStateFlow<State<Unit>>,
    resultFlow: MutableStateFlow<List<T>>,
    useCaseCall: (size: Int) -> Flow<Resource<List<T>>>
) {
    useCaseCall(pageSize).onEach { result ->
        stateFlow.value = when (result) {
            is Resource.Loading -> State(loading = true)
            is Resource.Error -> State(error = result.error)
            is Resource.Success -> {
                val data = result.data ?: emptyList()
                anyNewItems(data.size >= pageSize)
                resultFlow.value = if (reset) data else resultFlow.value + data
                State(data = Unit)
            }
        }
    }.launchIn(viewModelScope)
}