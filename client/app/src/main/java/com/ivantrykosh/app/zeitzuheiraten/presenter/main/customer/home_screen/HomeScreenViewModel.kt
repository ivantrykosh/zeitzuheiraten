package com.ivantrykosh.app.zeitzuheiraten.presenter.main.customer.home_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Post
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.posts.GetPostsByCategoryUseCase
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import com.ivantrykosh.app.zeitzuheiraten.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val getPostsByCategoryUseCase: GetPostsByCategoryUseCase,
) : ViewModel() {

    var getPosts = MutableStateFlow(State<List<Post>>())
        private set

    private var pageIndex = 0
    private var pageSize = 10

    fun getPostsByCategory(category: String) {
        getPostsByCategoryUseCase(category, pageIndex, pageSize).onEach { result ->
            getPosts.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> State(data = result.data)
            }
        }.launchIn(viewModelScope)
    }
}