package com.ivantrykosh.app.zeitzuheiraten.data.repository

import com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.firestore.FirestoreBookings
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Booking
import com.ivantrykosh.app.zeitzuheiraten.domain.model.DatePair
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.BookingRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.BookingStatus
import com.ivantrykosh.app.zeitzuheiraten.utils.BookingsFilterType
import javax.inject.Inject

class BookingRepositoryImpl @Inject constructor(
    private val firebaseBooking: FirestoreBookings,
) : BookingRepository {
    override suspend fun createBooking(userId: String, username: String, postId: String, category: String, providerId: String, provider: String, dateRange: DatePair) {
        firebaseBooking.createBooking(userId, username, postId, category, providerId, provider, dateRange)
    }

    override suspend fun createBookingWithLock(userId: String, username: String, postId: String, category: String, providerId: String, provider: String, dateRange: DatePair) {
        firebaseBooking.createBookingWithLock(userId, username, postId, category, providerId, provider, dateRange)
    }

    override suspend fun updateBookingDateRange(bookingId: String, dateRange: DatePair) {
        firebaseBooking.updateBookingDateRange(bookingId, dateRange)
    }

    override suspend fun updateBookingDateRangeWithLock(bookingId: String, dateRange: DatePair) {
        firebaseBooking.updateBookingDateRangeWithLock(bookingId, dateRange)
    }

    override suspend fun updateBookingStatus(bookingId: String, status: BookingStatus) {
        firebaseBooking.updateBookingStatus(bookingId, status)
    }

    override suspend fun getBookingsForUser(userId: String, startAfterLast: Boolean, pageSize: Int, bookingsFilterType: BookingsFilterType): List<Booking> {
        return firebaseBooking.getBookingsForUser(userId, startAfterLast, pageSize, bookingsFilterType)
    }

    override suspend fun getBookingsForPost(postId: String, startAfterLast: Boolean, pageSize: Int, bookingsFilterType: BookingsFilterType): List<Booking> {
        return firebaseBooking.getBookingsForPost(postId, startAfterLast, pageSize, bookingsFilterType)
    }

    override suspend fun getBookingDatesForPost(postId: String): List<DatePair> {
        return firebaseBooking.getBookingDatesForPost(postId)
    }
}