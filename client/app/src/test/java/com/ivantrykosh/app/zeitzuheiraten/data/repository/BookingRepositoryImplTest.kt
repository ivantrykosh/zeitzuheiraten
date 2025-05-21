package com.ivantrykosh.app.zeitzuheiraten.data.repository

import com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.firestore.FirestoreBookings
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Booking
import com.ivantrykosh.app.zeitzuheiraten.domain.model.DatePair
import com.ivantrykosh.app.zeitzuheiraten.utils.BookingStatus
import com.ivantrykosh.app.zeitzuheiraten.utils.BookingsFilterType
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class BookingRepositoryImplTest {

    private lateinit var mockFirestoreBookings: FirestoreBookings
    private lateinit var bookingRepositoryImpl: BookingRepositoryImpl

    @Before
    fun setup() {
        mockFirestoreBookings = mock()
        bookingRepositoryImpl = BookingRepositoryImpl(mockFirestoreBookings)
    }

    @Test
    fun `create booking successfully`() = runTest {
        val userId = "userId"
        val username = "User Name"
        val postId = "postId"
        val category = "Video"
        val providerId = "providerId"
        val provider = "Provider Name"
        val dateRange = DatePair(0L, 86400L)
        whenever(mockFirestoreBookings.createBooking(userId, username, postId, category, providerId, provider, dateRange)).doReturn(Unit)

        bookingRepositoryImpl.createBooking(userId, username, postId, category, providerId, provider, dateRange)

        verify(mockFirestoreBookings).createBooking(userId, username, postId, category, providerId, provider, dateRange)
    }

    @Test
    fun `create booking with lock successfully`() = runTest {
        val userId = "userId"
        val username = "User Name"
        val postId = "postId"
        val category = "Video"
        val providerId = "providerId"
        val provider = "Provider Name"
        val dateRange = DatePair(0L, 86400L)
        whenever(mockFirestoreBookings.createBookingWithLock(userId, username, postId, category, providerId, provider, dateRange)).doReturn(Unit)

        bookingRepositoryImpl.createBookingWithLock(userId, username, postId, category, providerId, provider, dateRange)

        verify(mockFirestoreBookings).createBookingWithLock(userId, username, postId, category, providerId, provider, dateRange)
    }

    @Test
    fun `update booking date range successfully`() = runTest {
        val bookingId = "bookingId"
        val dateRange = DatePair(0L, 86400L)
        whenever(mockFirestoreBookings.updateBookingDateRange(bookingId, dateRange)).doReturn(Unit)

        bookingRepositoryImpl.updateBookingDateRange(bookingId, dateRange)

        verify(mockFirestoreBookings).updateBookingDateRange(bookingId, dateRange)
    }

    @Test
    fun `update booking date range with lock successfully`() = runTest {
        val bookingId = "bookingId"
        val dateRange = DatePair(0L, 86400L)
        whenever(mockFirestoreBookings.updateBookingDateRangeWithLock(bookingId, dateRange)).doReturn(Unit)

        bookingRepositoryImpl.updateBookingDateRangeWithLock(bookingId, dateRange)

        verify(mockFirestoreBookings).updateBookingDateRangeWithLock(bookingId, dateRange)
    }

    @Test
    fun `update booking status successfully`() = runTest {
        val bookingId = "bookingId"
        val status = BookingStatus.CONFIRMED
        whenever(mockFirestoreBookings.updateBookingStatus(bookingId, status)).doReturn(Unit)

        bookingRepositoryImpl.updateBookingStatus(bookingId, status)

        verify(mockFirestoreBookings).updateBookingStatus(bookingId, status)
    }

    @Test
    fun `get bookings for user successfully`() = runTest {
        val userId = "userId"
        val startAfterLast = false
        val pageSize = 10
        val bookingsFilterType = BookingsFilterType.CANCELED
        val expectedBookings = listOf(Booking(id = "booking1"), Booking(id = "booking2"))
        whenever(mockFirestoreBookings.getBookingsForUser(userId, startAfterLast, pageSize, bookingsFilterType)).doReturn(expectedBookings)

        val actualBookings = bookingRepositoryImpl.getBookingsForUser(userId, startAfterLast, pageSize, bookingsFilterType)

        verify(mockFirestoreBookings).getBookingsForUser(userId, startAfterLast, pageSize, bookingsFilterType)
        Assert.assertEquals(expectedBookings, actualBookings)
    }

    @Test
    fun `get bookings for post successfully`() = runTest {
        val postId = "postId"
        val startAfterLast = false
        val pageSize = 10
        val bookingsFilterType = BookingsFilterType.CANCELED
        val expectedBookings = listOf(Booking(id = "booking1"), Booking(id = "booking2"))
        whenever(mockFirestoreBookings.getBookingsForPost(postId, startAfterLast, pageSize, bookingsFilterType)).doReturn(expectedBookings)

        val actualBookings = bookingRepositoryImpl.getBookingsForPost(postId, startAfterLast, pageSize, bookingsFilterType)

        verify(mockFirestoreBookings).getBookingsForPost(postId, startAfterLast, pageSize, bookingsFilterType)
        Assert.assertEquals(expectedBookings, actualBookings)
    }

    @Test
    fun `get booking dates for post successfully`() = runTest {
        val postId = "postId"
        val expectedDates = listOf(DatePair(), DatePair())
        whenever(mockFirestoreBookings.getBookingDatesForPost(postId)).doReturn(expectedDates)

        val actualDates = bookingRepositoryImpl.getBookingDatesForPost(postId)

        verify(mockFirestoreBookings).getBookingDatesForPost(postId)
        Assert.assertEquals(expectedDates, actualDates)
    }
}