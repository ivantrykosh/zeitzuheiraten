package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.bookings

import android.util.Log
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Booking
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.BookingRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.BookingsFilterType
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

private const val LOG_TAG = "GetBookingsForPostUseCase"

class GetBookingsForPostUseCase @Inject constructor(
    private val bookingRepository: BookingRepository
) {
    operator fun invoke(postId: String, startAfterLast: Boolean, pageSize: Int, bookingsFilterType: BookingsFilterType) = flow<Resource<List<Booking>>> {
        try {
            emit(Resource.Loading())
            val bookings = bookingRepository.getBookingsForPost(postId, startAfterLast, pageSize, bookingsFilterType)
            emit(Resource.Success(bookings))
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message ?: "An error occurred")
            emit(Resource.Error(e))
        }
    }
}