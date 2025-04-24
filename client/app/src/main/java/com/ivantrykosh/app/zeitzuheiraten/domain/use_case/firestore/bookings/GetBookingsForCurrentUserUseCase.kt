package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.bookings

import com.ivantrykosh.app.zeitzuheiraten.domain.model.Booking
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.BookingRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserAuthRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.BookingsFilterType
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetBookingsForCurrentUserUseCase @Inject constructor(
    private val userAuthRepository: UserAuthRepository,
    private val bookingRepository: BookingRepository
) {
    operator fun invoke(startAfterLast: Boolean, pageSize: Int, bookingsFilterType: BookingsFilterType) = flow<Resource<List<Booking>>> {
        try {
            emit(Resource.Loading())
            val userId = userAuthRepository.getCurrentUserId()
            val bookings = bookingRepository.getBookingsForUser(userId, startAfterLast, pageSize, bookingsFilterType)
            emit(Resource.Success(bookings))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }
}