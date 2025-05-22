package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.bookings

import android.util.Log
import com.ivantrykosh.app.zeitzuheiraten.domain.model.DatePair
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.BookingRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserAuthRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

private const val LOG_TAG = "CreateBookingUseCase"

class CreateBookingUseCase @Inject constructor(
    private val userAuthRepository: UserAuthRepository,
    private val userRepository: UserRepository,
    private val bookingRepository: BookingRepository,
) {
    /**
     * Create booking. For common categories [withLock] must be true and for the other categories it must be false
     */
    operator fun invoke(postId: String, category: String, providerId: String, provider: String, dateRange: DatePair, withLock: Boolean) = flow<Resource<Unit>> {
        try {
            emit(Resource.Loading())
            val userId = userAuthRepository.getCurrentUserId()
            val user = userRepository.getUserById(userId)!!
            if (withLock) {
                bookingRepository.createBookingWithLock(userId, user.name, postId, category, providerId, provider, dateRange)
            } else {
                bookingRepository.createBooking(userId, user.name, postId, category, providerId, provider, dateRange)
            }
            emit(Resource.Success())
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message ?: "An error occurred")
            emit(Resource.Error(e))
        }
    }
}