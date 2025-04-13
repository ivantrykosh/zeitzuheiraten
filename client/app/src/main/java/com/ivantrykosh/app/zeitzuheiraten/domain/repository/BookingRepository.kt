package com.ivantrykosh.app.zeitzuheiraten.domain.repository

import com.ivantrykosh.app.zeitzuheiraten.domain.model.Booking
import com.ivantrykosh.app.zeitzuheiraten.domain.model.DatePair

interface BookingRepository {

    suspend fun createBooking(userId: String, username: String, postId: String, category: String, provider: String, dateRange: DatePair)

    suspend fun updateBooking(bookingId: String, dateRange: DatePair?, confirmed: Boolean?, canceled: Boolean?, serviceProvided: Boolean?)

    suspend fun getBookingsForUser(userId: String, startAfterLast: Boolean, pageSize: Int): List<Booking>

    suspend fun getBookingsForPost(postId: String, startAfterLast: Boolean, pageSize: Int): List<Booking>

    suspend fun getBookingDatesForPost(postId: String): List<DatePair>
}