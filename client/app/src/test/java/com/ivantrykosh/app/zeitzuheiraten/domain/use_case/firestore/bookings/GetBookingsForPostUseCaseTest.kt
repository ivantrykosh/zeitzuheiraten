package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.bookings

import com.ivantrykosh.app.zeitzuheiraten.data.repository.BookingRepositoryImpl
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Booking
import com.ivantrykosh.app.zeitzuheiraten.utils.BookingsFilterType
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
class GetBookingsForPostUseCaseTest {

    private lateinit var bookingRepositoryImpl: BookingRepositoryImpl
    private lateinit var getBookingsForPostUseCase: GetBookingsForPostUseCase

    @Before
    fun setup() {
        bookingRepositoryImpl = mock()
        getBookingsForPostUseCase = GetBookingsForPostUseCase(bookingRepositoryImpl)
    }

    @Test
    fun `get bookings for post successfully`() = runBlocking {
        val startAfterLast = false
        val pageSize = 20
        val bookingsFilterType = BookingsFilterType.CONFIRMED
        val postId = "postId"
        var resourceSuccess = false
        var actualBookings = listOf<Booking>()
        val expectedBookings = listOf<Booking>(Booking())
        whenever(bookingRepositoryImpl.getBookingsForPost(postId, startAfterLast, pageSize, bookingsFilterType)).doReturn(expectedBookings)

        getBookingsForPostUseCase(postId, startAfterLast, pageSize, bookingsFilterType).collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { Assert.fail(result.error.message) }
                is Resource.Success -> {
                    resourceSuccess = true
                    actualBookings = result.data!!
                }
            }
        }

        verify(bookingRepositoryImpl).getBookingsForPost(postId, startAfterLast, pageSize, bookingsFilterType)
        Assert.assertTrue(resourceSuccess)
        Assert.assertEquals(expectedBookings, actualBookings)
    }

    @Test(expected = CancellationException::class)
    fun `get bookings for post first emit must be loading`() = runBlocking {
        val startAfterLast = false
        val pageSize = 20
        val bookingsFilterType = BookingsFilterType.CONFIRMED
        val postId = "postId"

        getBookingsForPostUseCase(postId, startAfterLast, pageSize, bookingsFilterType).collect { result ->
            when (result) {
                is Resource.Loading -> { this.cancel() }
                is Resource.Error -> { Assert.fail("Loading must be first") }
                is Resource.Success -> { Assert.fail("Loading must be first") }
            }
        }
    }
}