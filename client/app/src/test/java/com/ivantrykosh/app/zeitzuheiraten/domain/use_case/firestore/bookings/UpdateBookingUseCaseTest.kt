package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.bookings

import com.ivantrykosh.app.zeitzuheiraten.data.repository.BookingRepositoryImpl
import com.ivantrykosh.app.zeitzuheiraten.domain.model.DatePair
import com.ivantrykosh.app.zeitzuheiraten.utils.BookingStatus
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
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
class UpdateBookingUseCaseTest {

    private lateinit var bookingRepositoryImpl: BookingRepositoryImpl
    private lateinit var updateBookingUseCase: UpdateBookingUseCase

    @Before
    fun setup() {
        bookingRepositoryImpl = mock()
        updateBookingUseCase = UpdateBookingUseCase(bookingRepositoryImpl)
    }

    @Test
    fun `update booking date successfully`() = runBlocking {
        val bookingId = "bookingId"
        val dateRange = DatePair(0L, 86400000L)
        val withLock = false
        var resourceSuccess = false
        whenever(bookingRepositoryImpl.updateBookingDateRange(bookingId, dateRange)).doReturn(Unit)

        updateBookingUseCase(bookingId, dateRange, null, withLock).collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { Assert.fail(result.error.message) }
                is Resource.Success -> { resourceSuccess = true }
            }
        }

        verify(bookingRepositoryImpl).updateBookingDateRange(bookingId, dateRange)
        Assert.assertTrue(resourceSuccess)
    }

    @Test
    fun `update booking date with lock successfully`() = runBlocking {
        val bookingId = "bookingId"
        val dateRange = DatePair(0L, 86400000L)
        val withLock = true
        var resourceSuccess = false
        whenever(bookingRepositoryImpl.updateBookingDateRangeWithLock(bookingId, dateRange)).doReturn(Unit)

        updateBookingUseCase(bookingId, dateRange, null, withLock).collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { Assert.fail(result.error.message) }
                is Resource.Success -> { resourceSuccess = true }
            }
        }

        verify(bookingRepositoryImpl).updateBookingDateRangeWithLock(bookingId, dateRange)
        Assert.assertTrue(resourceSuccess)
    }

    @Test
    fun `update booking status successfully`() = runBlocking {
        val bookingId = "bookingId"
        val status = BookingStatus.CONFIRMED
        val withLock = false
        var resourceSuccess = false
        whenever(bookingRepositoryImpl.updateBookingStatus(bookingId, status)).doReturn(Unit)

        updateBookingUseCase(bookingId, null, status, withLock).collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { Assert.fail(result.error.message) }
                is Resource.Success -> { resourceSuccess = true }
            }
        }

        verify(bookingRepositoryImpl).updateBookingStatus(bookingId, status)
        Assert.assertTrue(resourceSuccess)
    }

    @Test(expected = CancellationException::class)
    fun `update booking first emit must be loading`() = runBlocking {
        val bookingId = "bookingId"
        val withLock = false

        updateBookingUseCase(bookingId, null, null, withLock).collect { result ->
            when (result) {
                is Resource.Loading -> { this.cancel() }
                is Resource.Error -> { Assert.fail("Loading must be first") }
                is Resource.Success -> { Assert.fail("Loading must be first") }
            }
        }
    }
}