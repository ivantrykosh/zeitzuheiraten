package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.bookings

import android.util.Log
import com.ivantrykosh.app.zeitzuheiraten.domain.model.DatePair
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.BookingRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

private const val LOG_TAG = "UpdateBookingUseCase"

class UpdateBookingUseCase @Inject constructor(
    private val bookingRepository: BookingRepository,
) {
    operator fun invoke(bookingId: String, dateRange: DatePair? = null, confirmed: Boolean? = null, canceled: Boolean? = null, serviceProvided: Boolean? = null) = flow<Resource<Unit>> {
        try {
            emit(Resource.Loading())
            bookingRepository.updateBooking(bookingId, dateRange, confirmed, canceled, serviceProvided)
            emit(Resource.Success())
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message ?: "An error occurred")
            emit(Resource.Error(e))
        }
    }
}