package com.ivantrykosh.app.zeitzuheiraten.presenter.main.customer.my_bookings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Booking
import com.ivantrykosh.app.zeitzuheiraten.domain.model.DatePair
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.bookings.GetBookingsForCurrentUserUseCase
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.bookings.GetNotAvailableDatesForBookingUseCase
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.bookings.UpdateBookingUseCase
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.feedbacks.CreateFeedbackUseCase
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
class MyBookingsViewModel @Inject constructor(
    private val getBookingsForCurrentUserUseCase: GetBookingsForCurrentUserUseCase,
    private val getNotAvailableDatesForBookingUseCase: GetNotAvailableDatesForBookingUseCase,
    private val updateBookingUseCase: UpdateBookingUseCase,
    private val createFeedbackUseCase: CreateFeedbackUseCase,
) : ViewModel() {

    var getBookings = MutableStateFlow(State<Unit>())
        private set

    var lastBookings = MutableStateFlow(emptyList<Booking>())
        private set

    var anyNewBookings: Boolean = true
        private set

    var getNotAvailableDatesState = MutableStateFlow(State<List<DatePair>>())
        private set

    var changeDateState = MutableStateFlow(State<Unit>())
        private set

    var cancelBookingState = MutableStateFlow(State<Unit>())
        private set

    var confirmProvidingState = MutableStateFlow(State<Unit>())
        private set

    var createFeedbackState = MutableStateFlow(State<Unit>())
        private set

    private var pageSize = 10

    fun clearGetBookingsState() = clearState(getBookings)

    fun clearLastBookings() {
        lastBookings.value = emptyList()
    }

    fun clearGetNotAvailableDatesState() = clearState(getNotAvailableDatesState)

    fun clearChangeDateState() = clearState(changeDateState)

    fun clearCancelBookingState() = clearState(cancelBookingState)

    fun clearConfirmProvidingState() = clearState(confirmProvidingState)

    fun clearCreateFeedbackState() = clearState(createFeedbackState)

    fun getBookings(bookingsFilterType: BookingsFilterType, reset: Boolean) {
        loadPaginatedData(
            reset = reset,
            pageSize = pageSize,
            anyNewItems = { anyNewBookings = it },
            stateFlow = getBookings,
            resultFlow = lastBookings,
            useCaseCall = { size -> getBookingsForCurrentUserUseCase(!reset, size, bookingsFilterType) }
        )
    }

    fun getNotAvailableDates(id: String, availableDates: DatePair, includeBookingDates: Boolean) {
        getNotAvailableDatesForBookingUseCase(id, includeBookingDates).onEach { result ->
            getNotAvailableDatesState.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> {
                    State(data = result.data!!.minus(availableDates))
                }
            }
        }.launchIn(viewModelScope)
    }

    fun changeDate(bookingId: String, newDate: DatePair, withLock: Boolean) {
        updateBookingUseCase(bookingId = bookingId, dateRange = newDate, withLock = withLock).onEach { result ->
            changeDateState.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> {
                    lastBookings.value = lastBookings.value.map { booking ->
                        if (booking.id == bookingId) booking.copy(dateRange = newDate) else booking
                    }
                    State(data = Unit)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun cancelBooking(bookingId: String) {
        updateBookingUseCase(bookingId = bookingId, bookingStatus = BookingStatus.CANCELED, withLock = true).onEach { result ->
            cancelBookingState.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> {
                    lastBookings.value = lastBookings.value.filterNot { it.id == bookingId }
                    State(data = Unit)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun confirmServiceProviding(bookingId: String) {
        updateBookingUseCase(bookingId = bookingId, bookingStatus = BookingStatus.SERVICE_PROVIDED, withLock = true).onEach { result ->
            confirmProvidingState.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> {
                    lastBookings.value = lastBookings.value.filterNot { it.id == bookingId }
                    State(data = Unit)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun createFeedback(postId: String, category: String, provider: String, rating: Int, description: String) {
        createFeedbackUseCase(postId, category, provider, rating, description).onEach { result ->
            createFeedbackState.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> State(data = Unit)
            }
        }.launchIn(viewModelScope)
    }
}