package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.bookings

import com.ivantrykosh.app.zeitzuheiraten.domain.model.DatePair
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.BookingRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateBookingUseCase @Inject constructor(
    private val bookingRepository: BookingRepository,
) {
    operator fun invoke(bookingId: String, dateRange: DatePair? = null, confirmed: Boolean? = null, canceled: Boolean? = null, serviceProvided: Boolean? = null) = flow<Resource<Unit>> {
        try {
            emit(Resource.Loading())
            bookingRepository.updateBooking(bookingId, dateRange, confirmed, canceled, serviceProvided)
            emit(Resource.Success())
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }
}