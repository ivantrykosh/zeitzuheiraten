package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.bookings

import com.ivantrykosh.app.zeitzuheiraten.domain.model.DatePair
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.BookingRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.PostRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetNotAvailableDatesForBookingUseCase @Inject constructor(
    private val postRepository: PostRepository,
    private val bookingRepository: BookingRepository,
) {
    operator fun invoke(postId: String, includeBookingDates: Boolean = true) = flow<Resource<List<DatePair>>> {
        try {
            emit(Resource.Loading())
            val notAvailableDates = postRepository.getPostById(postId).notAvailableDates
            val allNotAvailableDates = if (includeBookingDates) {
                val bookedDates = bookingRepository.getBookingDatesForPost(postId)
                bookedDates.plus(notAvailableDates)
            } else {
                notAvailableDates
            }
            emit(Resource.Success(allNotAvailableDates))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }
}