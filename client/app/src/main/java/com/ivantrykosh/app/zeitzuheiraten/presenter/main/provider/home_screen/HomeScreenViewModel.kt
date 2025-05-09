package com.ivantrykosh.app.zeitzuheiraten.presenter.main.provider.home_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivantrykosh.app.zeitzuheiraten.domain.model.PostWithRating
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.posts.GetPostsForCurrentUserUseCase
import com.ivantrykosh.app.zeitzuheiraten.presenter.clearState
import com.ivantrykosh.app.zeitzuheiraten.utils.Constants.MAX_POSTS_PER_USER
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import com.ivantrykosh.app.zeitzuheiraten.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val getPostsForCurrentUserUseCase: GetPostsForCurrentUserUseCase,
) : ViewModel() {

    var getPostsState = MutableStateFlow(State<List<PostWithRating>>())
        private set

    fun clearGetPostsState() = clearState(getPostsState)

    fun getPosts() {
        getPostsForCurrentUserUseCase().onEach { result ->
            getPostsState.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> State(data = result.data!!)
            }
        }.launchIn(viewModelScope)
    }

    fun isLimitOfPostsReached(): Boolean {
        return (getPostsState.value.data?.size ?: 0) > MAX_POSTS_PER_USER
    }
}