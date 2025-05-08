package com.ivantrykosh.app.zeitzuheiraten.domain.repository

import com.ivantrykosh.app.zeitzuheiraten.domain.model.Booking
import com.ivantrykosh.app.zeitzuheiraten.domain.model.DatePair
import com.ivantrykosh.app.zeitzuheiraten.utils.BookingStatus
import com.ivantrykosh.app.zeitzuheiraten.utils.BookingsFilterType

interface BookingRepository {

    suspend fun createBooking(userId: String, username: String, postId: String, category: String, providerId: String, provider: String, dateRange: DatePair)

    suspend fun createBookingWithLock(userId: String, username: String, postId: String, category: String, providerId: String, provider: String, dateRange: DatePair)

    suspend fun updateBookingDateRange(bookingId: String, dateRange: DatePair)

    suspend fun updateBookingDateRangeWithLock(bookingId: String, dateRange: DatePair)

    suspend fun updateBookingStatus(bookingId: String, status: BookingStatus)

    suspend fun getBookingsForUser(userId: String, startAfterLast: Boolean, pageSize: Int, bookingsFilterType: BookingsFilterType): List<Booking>

    suspend fun getBookingsForPost(postId: String, startAfterLast: Boolean, pageSize: Int, bookingsFilterType: BookingsFilterType): List<Booking>

    suspend fun getBookingDatesForPost(postId: String): List<DatePair>
}