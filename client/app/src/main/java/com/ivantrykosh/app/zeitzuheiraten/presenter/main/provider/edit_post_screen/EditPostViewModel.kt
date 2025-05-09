package com.ivantrykosh.app.zeitzuheiraten.presenter.main.provider.edit_post_screen

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivantrykosh.app.zeitzuheiraten.domain.model.DatePair
import com.ivantrykosh.app.zeitzuheiraten.domain.model.PostWithRating
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.posts.DeletePostByIdUseCase
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.posts.GetPostByIdUseCase
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.posts.UpdatePostUseCase
import com.ivantrykosh.app.zeitzuheiraten.presenter.clearState
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import com.ivantrykosh.app.zeitzuheiraten.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class EditPostViewModel @Inject constructor(
    private val updatePostUseCase: UpdatePostUseCase,
    private val getPostByIdUseCase: GetPostByIdUseCase,
    private val deletePostByIdUseCase: DeletePostByIdUseCase,
) : ViewModel() {

    var updatePostState = MutableStateFlow(State<Unit>())
        private set

    var getPostByIdState = MutableStateFlow(State<PostWithRating>())
        private set

    var deletePostState = MutableStateFlow(State<Unit>())
        private set

    private lateinit var previousImages: List<String>

    fun clearUpdatePostState() = clearState(updatePostState)

    fun clearGetPostByIdState() = clearState(getPostByIdState)

    fun clearDeletePostState() = clearState(deletePostState)

    fun updatePost(id: String, cities: List<String>, minPrice: Int, description: String, notAvailableDates: List<DatePair>, images: List<Uri>, uploadNewImages: Boolean, enabled: Boolean) {
        updatePostUseCase(id, cities, minPrice, description, notAvailableDates, images, previousImages, uploadNewImages, enabled).onEach { result ->
            updatePostState.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> State(data = Unit)
            }
        }.launchIn(viewModelScope)
    }

    fun getPostById(id: String) {
        getPostByIdUseCase(id).onEach { result ->
            getPostByIdState.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> {
                    previousImages = result.data!!.photosUrl
                    State(data = result.data)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun deletePostState(id: String) {
        deletePostByIdUseCase(id).onEach { result ->
            deletePostState.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> State(data = Unit)
            }
        }.launchIn(viewModelScope)
    }
}