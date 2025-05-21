package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.bookings

import com.ivantrykosh.app.zeitzuheiraten.data.repository.BookingRepositoryImpl
import com.ivantrykosh.app.zeitzuheiraten.data.repository.PostRepositoryImpl
import com.ivantrykosh.app.zeitzuheiraten.domain.model.DatePair
import com.ivantrykosh.app.zeitzuheiraten.domain.model.PostWithRating
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
class GetNotAvailableDatesForBookingUseCaseTest {

    private lateinit var postRepositoryImpl: PostRepositoryImpl
    private lateinit var bookingRepositoryImpl: BookingRepositoryImpl
    private lateinit var getNotAvailableDatesForBookingUseCase: GetNotAvailableDatesForBookingUseCase

    @Before
    fun setup() {
        postRepositoryImpl = mock()
        bookingRepositoryImpl = mock()
        getNotAvailableDatesForBookingUseCase = GetNotAvailableDatesForBookingUseCase(postRepositoryImpl, bookingRepositoryImpl)
    }

    @Test
    fun `get not available dates for booking successfully`() = runBlocking {
        val postId = "postId"
        val includeBookingDates = false
        var resourceSuccess = false
        val post = PostWithRating(notAvailableDates = listOf(DatePair(0L, 86400000L)))
        var actualDates = listOf<DatePair>()
        val expectedDates = listOf<DatePair>(DatePair(0L, 86400000L))
        whenever(postRepositoryImpl.getPostById(postId)).doReturn(post)

        getNotAvailableDatesForBookingUseCase(postId, includeBookingDates).collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { Assert.fail(result.error.message) }
                is Resource.Success -> {
                    resourceSuccess = true
                    actualDates = result.data!!
                }
            }
        }

        verify(postRepositoryImpl).getPostById(postId)
        Assert.assertTrue(resourceSuccess)
        Assert.assertEquals(expectedDates, actualDates)
    }

    @Test
    fun `get not available dates for booking include booking dates successfully`() = runBlocking {
        val postId = "postId"
        val includeBookingDates = true
        var resourceSuccess = false
        val post = PostWithRating(notAvailableDates = listOf(DatePair(0L, 86400000L)))
        var actualDates = listOf<DatePair>()
        val bookingDates = listOf(DatePair(86400000L, 172800000L))
        val expectedDates = bookingDates.plus(post.notAvailableDates)
        whenever(postRepositoryImpl.getPostById(postId)).doReturn(post)
        whenever(bookingRepositoryImpl.getBookingDatesForPost(postId)).doReturn(bookingDates)

        getNotAvailableDatesForBookingUseCase(postId, includeBookingDates).collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { Assert.fail(result.error.message) }
                is Resource.Success -> {
                    resourceSuccess = true
                    actualDates = result.data!!
                }
            }
        }

        verify(postRepositoryImpl).getPostById(postId)
        verify(bookingRepositoryImpl).getBookingDatesForPost(postId)
        Assert.assertTrue(resourceSuccess)
        Assert.assertEquals(expectedDates, actualDates)
    }

    @Test(expected = CancellationException::class)
    fun `get not available dates for booking first emit must be loading`() = runBlocking {
        val postId = "postId"

        getNotAvailableDatesForBookingUseCase(postId).collect { result ->
            when (result) {
                is Resource.Loading -> { this.cancel() }
                is Resource.Error -> { Assert.fail("Loading must be first") }
                is Resource.Success -> { Assert.fail("Loading must be first") }
            }
        }
    }
}