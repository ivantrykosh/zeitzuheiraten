package com.ivantrykosh.app.zeitzuheiraten.presenter.main.provider.bookings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Booking
import com.ivantrykosh.app.zeitzuheiraten.domain.model.PostWithRating
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.bookings.GetBookingsForPostUseCase
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.bookings.UpdateBookingUseCase
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.posts.GetPostsForCurrentUserUseCase
import com.ivantrykosh.app.zeitzuheiraten.utils.BookingsFilterType
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import com.ivantrykosh.app.zeitzuheiraten.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class BookingsViewModel @Inject constructor(
    private val getPostsForCurrentUserUseCase: GetPostsForCurrentUserUseCase,
    private val getBookingsForPostUseCase: GetBookingsForPostUseCase,
    private val updateBookingUseCase: UpdateBookingUseCase,
) : ViewModel() {

    var getPostsState = MutableStateFlow(State<List<PostWithRating>>())
        private set

    var getBookings = MutableStateFlow(State<List<Booking>>())
        private set

    var lastBookings = MutableStateFlow(emptyList<Booking>())
        private set

    var anyNewBookings: Boolean = true
        private set

    var cancelBookingState = MutableStateFlow(State<Unit>())
        private set

    var confirmBookingState = MutableStateFlow(State<Unit>())
        private set

    private var pageSize = 10

    init {
        getPosts()
    }

    fun getPosts() {
        getPostsForCurrentUserUseCase().onEach { result ->
            getPostsState.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> State(data = result.data!!)
            }
        }.launchIn(viewModelScope)
    }

    fun clearLastBookings() {
        lastBookings.value = emptyList()
    }

    fun clearCancelBookingState() {
        cancelBookingState.value = State()
    }

    fun clearConfirmBookingState() {
        confirmBookingState.value = State()
    }

    fun getBookingsForPost(postId: String, bookingsFilterType: BookingsFilterType) {
        anyNewBookings = true
        getBookingsForPostUseCase(postId, false, pageSize, bookingsFilterType).onEach { result ->
            getBookings.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> {
                    if (result.data!!.size < pageSize) {
                        anyNewBookings = false
                    }
                    lastBookings.value = result.data
                    State(data = lastBookings.value)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getNewBookingsForPost(postId: String, bookingsFilterType: BookingsFilterType) {
        getBookingsForPostUseCase(postId, true, pageSize, bookingsFilterType).onEach { result ->
            getBookings.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> {
                    if (result.data!!.size < pageSize) {
                        anyNewBookings = false
                    }
                    lastBookings.value = lastBookings.value.plus(result.data)
                    State(data = lastBookings.value)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun cancelBooking(bookingId: String) {
        updateBookingUseCase(bookingId, canceled = true).onEach { result ->
            cancelBookingState.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> State(data = Unit)
            }
        }.launchIn(viewModelScope)
    }

    fun confirmBooking(bookingId: String) {
        updateBookingUseCase(bookingId = bookingId, confirmed = true).onEach { result ->
            confirmBookingState.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> State(data = Unit)
            }
        }.launchIn(viewModelScope)
    }
}