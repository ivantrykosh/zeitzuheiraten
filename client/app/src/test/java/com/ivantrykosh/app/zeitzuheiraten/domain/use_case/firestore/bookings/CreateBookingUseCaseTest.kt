package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.bookings

import com.ivantrykosh.app.zeitzuheiraten.data.repository.BookingRepositoryImpl
import com.ivantrykosh.app.zeitzuheiraten.data.repository.UserAuthRepositoryImpl
import com.ivantrykosh.app.zeitzuheiraten.data.repository.UserRepositoryImpl
import com.ivantrykosh.app.zeitzuheiraten.domain.model.DatePair
import com.ivantrykosh.app.zeitzuheiraten.domain.model.User
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
class CreateBookingUseCaseTest {

    private lateinit var userAuthRepositoryImpl: UserAuthRepositoryImpl
    private lateinit var userRepositoryImpl: UserRepositoryImpl
    private lateinit var bookingRepositoryImpl: BookingRepositoryImpl
    private lateinit var createBookingUseCase: CreateBookingUseCase

    @Before
    fun setup() {
        userAuthRepositoryImpl = mock()
        userRepositoryImpl = mock()
        bookingRepositoryImpl = mock()
        createBookingUseCase = CreateBookingUseCase(userAuthRepositoryImpl, userRepositoryImpl, bookingRepositoryImpl)
    }

    @Test
    fun `create booking successfully`() = runBlocking {
        val postId = "postId"
        val category = "Video"
        val providerId = "providerId"
        val provider = "Provider Name"
        val dateRange = DatePair(0L, 86400000L)
        val withLock = false
        val userId = "userId"
        val user = User(id = userId, name = "Test User", email = "test@email.com")
        var resourceSuccess = false
        whenever(userAuthRepositoryImpl.getCurrentUserId()).doReturn(userId)
        whenever(userRepositoryImpl.getUserById(userId)).doReturn(user)
        whenever(bookingRepositoryImpl.createBooking(userId, user.name, postId, category, providerId, provider, dateRange)).doReturn(Unit)

        createBookingUseCase(postId, category, providerId, provider, dateRange, withLock).collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { Assert.fail(result.error.message) }
                is Resource.Success -> { resourceSuccess = true }
            }
        }

        verify(userAuthRepositoryImpl).getCurrentUserId()
        verify(userRepositoryImpl).getUserById(userId)
        verify(bookingRepositoryImpl).createBooking(userId, user.name, postId, category, providerId, provider, dateRange)
        Assert.assertTrue(resourceSuccess)
    }

    @Test
    fun `create booking with lock successfully`() = runBlocking {
        val postId = "postId"
        val category = "Video"
        val providerId = "providerId"
        val provider = "Provider Name"
        val dateRange = DatePair(0L, 86400000L)
        val withLock = true
        val userId = "userId"
        val user = User(id = userId, name = "Test User", email = "test@email.com")
        var resourceSuccess = false
        whenever(userAuthRepositoryImpl.getCurrentUserId()).doReturn(userId)
        whenever(userRepositoryImpl.getUserById(userId)).doReturn(user)
        whenever(bookingRepositoryImpl.createBookingWithLock(userId, user.name, postId, category, providerId, provider, dateRange)).doReturn(Unit)

        createBookingUseCase(postId, category, providerId, provider, dateRange, withLock).collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { Assert.fail(result.error.message) }
                is Resource.Success -> { resourceSuccess = true }
            }
        }

        verify(userAuthRepositoryImpl).getCurrentUserId()
        verify(userRepositoryImpl).getUserById(userId)
        verify(bookingRepositoryImpl).createBookingWithLock(userId, user.name, postId, category, providerId, provider, dateRange)
        Assert.assertTrue(resourceSuccess)
    }

    @Test(expected = CancellationException::class)
    fun `create booking first emit must be loading`() = runBlocking {
        val postId = "postId"
        val category = "Video"
        val providerId = "providerId"
        val provider = "Provider Name"
        val dateRange = DatePair(0L, 86400000L)
        val withLock = false

        createBookingUseCase(postId, category, providerId, provider, dateRange, withLock).collect { result ->
            when (result) {
                is Resource.Loading -> { this.cancel() }
                is Resource.Error -> { Assert.fail("Loading must be first") }
                is Resource.Success -> { Assert.fail("Loading must be first") }
            }
        }
    }
}