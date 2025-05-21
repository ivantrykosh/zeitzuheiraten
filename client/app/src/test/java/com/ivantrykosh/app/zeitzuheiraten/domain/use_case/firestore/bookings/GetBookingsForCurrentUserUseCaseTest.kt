package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.bookings

import com.ivantrykosh.app.zeitzuheiraten.data.repository.BookingRepositoryImpl
import com.ivantrykosh.app.zeitzuheiraten.data.repository.UserAuthRepositoryImpl
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
class GetBookingsForCurrentUserUseCaseTest {

    private lateinit var userAuthRepositoryImpl: UserAuthRepositoryImpl
    private lateinit var bookingRepositoryImpl: BookingRepositoryImpl
    private lateinit var getBookingsForCurrentUserUseCase: GetBookingsForCurrentUserUseCase

    @Before
    fun setup() {
        userAuthRepositoryImpl = mock()
        bookingRepositoryImpl = mock()
        getBookingsForCurrentUserUseCase = GetBookingsForCurrentUserUseCase(userAuthRepositoryImpl, bookingRepositoryImpl)
    }

    @Test
    fun `get bookings for current user successfully`() = runBlocking {
        val startAfterLast = false
        val pageSize = 20
        val bookingsFilterType = BookingsFilterType.CONFIRMED
        val userId = "userId"
        var resourceSuccess = false
        var actualBookings = listOf<Booking>()
        val expectedBookings = listOf<Booking>(Booking())
        whenever(userAuthRepositoryImpl.getCurrentUserId()).doReturn(userId)
        whenever(bookingRepositoryImpl.getBookingsForUser(userId, startAfterLast, pageSize, bookingsFilterType)).doReturn(expectedBookings)

        getBookingsForCurrentUserUseCase(startAfterLast, pageSize, bookingsFilterType).collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { Assert.fail(result.error.message) }
                is Resource.Success -> {
                    resourceSuccess = true
                    actualBookings = result.data!!
                }
            }
        }

        verify(userAuthRepositoryImpl).getCurrentUserId()
        verify(bookingRepositoryImpl).getBookingsForUser(userId, startAfterLast, pageSize, bookingsFilterType)
        Assert.assertTrue(resourceSuccess)
        Assert.assertEquals(expectedBookings, actualBookings)
    }

    @Test(expected = CancellationException::class)
    fun `get bookings for current user first emit must be loading`() = runBlocking {
        val startAfterLast = false
        val pageSize = 20
        val bookingsFilterType = BookingsFilterType.CONFIRMED

        getBookingsForCurrentUserUseCase(startAfterLast, pageSize, bookingsFilterType).collect { result ->
            when (result) {
                is Resource.Loading -> { this.cancel() }
                is Resource.Error -> { Assert.fail("Loading must be first") }
                is Resource.Success -> { Assert.fail("Loading must be first") }
            }
        }
    }
}