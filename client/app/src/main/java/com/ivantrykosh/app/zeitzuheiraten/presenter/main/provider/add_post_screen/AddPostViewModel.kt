package com.ivantrykosh.app.zeitzuheiraten.presenter.main.provider.add_post_screen

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivantrykosh.app.zeitzuheiraten.domain.model.DatePair
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.posts.CreatePostUseCase
import com.ivantrykosh.app.zeitzuheiraten.presenter.clearState
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import com.ivantrykosh.app.zeitzuheiraten.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AddPostViewModel @Inject constructor(
    private val createPostUseCase: CreatePostUseCase,
) : ViewModel() {

    var createPostState = MutableStateFlow(State<Unit>())
        private set

    fun clearCreatePostState() = clearState(createPostState)

    fun createPost(category: String, cities: List<String>, minPrice: Int, description: String, notAvailableDates: List<DatePair>, images: List<Uri>) {
        createPostUseCase(category, cities, minPrice, description, notAvailableDates, images).onEach { result ->
            createPostState.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> State(data = Unit)
            }
        }.launchIn(viewModelScope)
    }
}