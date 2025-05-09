package com.ivantrykosh.app.zeitzuheiraten.presenter.main.customer.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivantrykosh.app.zeitzuheiraten.domain.model.DatePair
import com.ivantrykosh.app.zeitzuheiraten.domain.model.PostWithRating
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.bookings.CreateBookingUseCase
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.bookings.GetNotAvailableDatesForBookingUseCase
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.posts.GetPostByIdUseCase
import com.ivantrykosh.app.zeitzuheiraten.presenter.clearState
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
    private val getNotAvailableDatesForBookingUseCase: GetNotAvailableDatesForBookingUseCase,
    private val createBookingUseCase: CreateBookingUseCase,
) : ViewModel() {

    var getPostByIdState = MutableStateFlow(State<PostWithRating>())
        private set

    var getNotAvailableDatesState = MutableStateFlow(State<List<DatePair>>())
        private set

    var createBookingState = MutableStateFlow(State<Unit>())
        private set

    fun clearGetPostByIdState() = clearState(getPostByIdState)

    fun clearGetNotAvailableDatesState() = clearState(getNotAvailableDatesState)

    fun clearCreateBookingState() = clearState(createBookingState)

    fun getPostById(id: String) {
        getPostByIdUseCase(id).onEach { result ->
            getPostByIdState.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> State(data = result.data)
            }
        }.launchIn(viewModelScope)
    }

    fun getNotAvailableDates(id: String) {
        getNotAvailableDatesForBookingUseCase(id).onEach { result ->
            getNotAvailableDatesState.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> State(data = result.data)
            }
        }.launchIn(viewModelScope)
    }

    fun bookService(id: String, dateRange: DatePair, withLock: Boolean) {
        val post = getPostByIdState.value.data!!
        createBookingUseCase(id, post.category, post.providerId, post.providerName, dateRange, withLock).onEach { result ->
            createBookingState.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> State(data = Unit)
            }
        }.launchIn(viewModelScope)
    }
}