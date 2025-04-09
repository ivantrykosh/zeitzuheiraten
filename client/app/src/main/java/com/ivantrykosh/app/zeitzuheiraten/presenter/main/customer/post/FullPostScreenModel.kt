package com.ivantrykosh.app.zeitzuheiraten.presenter.main.customer.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Post
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.posts.GetPostByIdUseCase
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import com.ivantrykosh.app.zeitzuheiraten.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class FullPostScreenModel @Inject constructor(
    private val getPostByIdUseCase: GetPostByIdUseCase,
) : ViewModel() {

    var getPostByIdState = MutableStateFlow(State<Post>())
        private set

    fun getPostById(id: String) {
        getPostByIdUseCase(id).onEach { result ->
            getPostByIdState.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> State(data = result.data)
            }
        }.launchIn(viewModelScope)
    }

    fun bookService(id: String) {
        // todo book service
    }
}