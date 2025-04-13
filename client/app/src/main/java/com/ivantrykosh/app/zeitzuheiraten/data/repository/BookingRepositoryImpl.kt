package com.ivantrykosh.app.zeitzuheiraten.data.repository

import com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.firestore.FirestoreBookings
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Booking
import com.ivantrykosh.app.zeitzuheiraten.domain.model.DatePair
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.BookingRepository
import javax.inject.Inject

class BookingRepositoryImpl @Inject constructor(
    private val firebaseBooking: FirestoreBookings,
) : BookingRepository {
    override suspend fun createBooking(userId: String, username: String, postId: String, category: String, provider: String, dateRange: DatePair) {
        firebaseBooking.createBooking(userId, username, postId, category, provider, dateRange)
    }

    override suspend fun updateBooking(bookingId: String, dateRange: DatePair?, confirmed: Boolean?, canceled: Boolean?, serviceProvided: Boolean?) {
        firebaseBooking.updateBooking(bookingId, dateRange, confirmed, canceled, serviceProvided)
    }

    override suspend fun getBookingsForUser(userId: String, startAfterLast: Boolean, pageSize: Int): List<Booking> {
        return firebaseBooking.getBookingsForUser(userId, startAfterLast, pageSize)
    }

    override suspend fun getBookingsForPost(postId: String, startAfterLast: Boolean, pageSize: Int): List<Booking> {
        return firebaseBooking.getBookingsForPost(postId, startAfterLast, pageSize)
    }

    override suspend fun getBookingDatesForPost(postId: String): List<DatePair> {
        return firebaseBooking.getBookingDatesForPost(postId)
    }
}