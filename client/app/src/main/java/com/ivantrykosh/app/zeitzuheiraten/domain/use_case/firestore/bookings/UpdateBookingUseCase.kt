package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.bookings

import android.util.Log
import com.ivantrykosh.app.zeitzuheiraten.domain.model.DatePair
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.BookingRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.BookingStatus
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

private const val LOG_TAG = "UpdateBookingUseCase"

class UpdateBookingUseCase @Inject constructor(
    private val bookingRepository: BookingRepository,
) {
    operator fun invoke(bookingId: String, dateRange: DatePair? = null, bookingStatus: BookingStatus? = null, withLock: Boolean) = flow<Resource<Unit>> {
        try {
            emit(Resource.Loading())
            if (dateRange != null) {
                if (withLock) {
                    bookingRepository.updateBookingDateRangeWithLock(bookingId, dateRange)
                } else {
                    bookingRepository.updateBookingDateRange(bookingId, dateRange)
                }
            } else if (bookingStatus != null) {
                bookingRepository.updateBookingStatus(bookingId, bookingStatus)
            }
            emit(Resource.Success())
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message ?: "An error occurred")
            emit(Resource.Error(e))
        }
    }
}