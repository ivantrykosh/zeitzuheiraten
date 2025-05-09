package com.ivantrykosh.app.zeitzuheiraten.presenter.main.provider.bookings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Booking
import com.ivantrykosh.app.zeitzuheiraten.domain.model.PostWithRating
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.bookings.GetBookingsForPostUseCase
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.bookings.UpdateBookingUseCase
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.posts.GetPostsForCurrentUserUseCase
import com.ivantrykosh.app.zeitzuheiraten.presenter.clearState
import com.ivantrykosh.app.zeitzuheiraten.presenter.loadPaginatedData
import com.ivantrykosh.app.zeitzuheiraten.utils.BookingStatus
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

    var getBookings = MutableStateFlow(State<Unit>())
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

    fun clearGetPostsState() = clearState(getPostsState)

    fun clearGetBookingsState() = clearState(getBookings)

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

    fun clearCancelBookingState() = clearState(cancelBookingState)

    fun clearConfirmBookingState() = clearState(confirmBookingState)

    fun getBookingsForPost(postId: String, bookingsFilterType: BookingsFilterType, reset: Boolean) {
        loadPaginatedData(
            reset = reset,
            pageSize = pageSize,
            anyNewItems = { anyNewBookings = it },
            stateFlow = getBookings,
            resultFlow = lastBookings,
            useCaseCall = { size -> getBookingsForPostUseCase(postId, !reset, size, bookingsFilterType) }
        )
    }

    fun cancelBooking(bookingId: String) {
        updateBookingUseCase(bookingId = bookingId, bookingStatus = BookingStatus.CANCELED, withLock = true).onEach { result ->
            cancelBookingState.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> State(data = Unit)
            }
        }.launchIn(viewModelScope)
    }

    fun confirmBooking(bookingId: String) {
        updateBookingUseCase(bookingId = bookingId, bookingStatus = BookingStatus.CONFIRMED, withLock = true).onEach { result ->
            confirmBookingState.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> State(data = Unit)
            }
        }.launchIn(viewModelScope)
    }
}