package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.feedbacks

import com.ivantrykosh.app.zeitzuheiraten.data.repository.UserAuthRepositoryImpl
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Feedback
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.FeedbackRepository
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
class GetFeedbacksForCurrentUserUseCaseTest {

    private lateinit var userAuthRepositoryImpl: UserAuthRepositoryImpl
    private lateinit var feedbackRepositoryImpl: FeedbackRepository
    private lateinit var getFeedbacksForCurrentUserUseCase: GetFeedbacksForCurrentUserUseCase

    @Before
    fun setup() {
        userAuthRepositoryImpl = mock()
        feedbackRepositoryImpl = mock()
        getFeedbacksForCurrentUserUseCase = GetFeedbacksForCurrentUserUseCase(userAuthRepositoryImpl, feedbackRepositoryImpl)
    }

    @Test
    fun `get feedbacks for current user successfully`() = runBlocking {
        val startAfterLast = false
        val pageSize = 20
        val userId = "userId"
        var resourceSuccess = false
        var actualFeedbacks = listOf<Feedback>()
        val expectedFeedbacks = listOf<Feedback>(Feedback())
        whenever(userAuthRepositoryImpl.getCurrentUserId()).doReturn(userId)
        whenever(feedbackRepositoryImpl.getFeedbacksForUser(userId, startAfterLast, pageSize)).doReturn(expectedFeedbacks)

        getFeedbacksForCurrentUserUseCase(startAfterLast, pageSize).collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { Assert.fail(result.error.message) }
                is Resource.Success -> {
                    resourceSuccess = true
                    actualFeedbacks = result.data!!
                }
            }
        }

        verify(userAuthRepositoryImpl).getCurrentUserId()
        verify(feedbackRepositoryImpl).getFeedbacksForUser(userId, startAfterLast, pageSize)
        Assert.assertTrue(resourceSuccess)
        Assert.assertEquals(expectedFeedbacks, actualFeedbacks)
    }

    @Test(expected = CancellationException::class)
    fun `get feedbacks for current user first emit must be loading`() = runBlocking {
        val startAfterLast = false
        val pageSize = 20

        getFeedbacksForCurrentUserUseCase(startAfterLast, pageSize).collect { result ->
            when (result) {
                is Resource.Loading -> { this.cancel() }
                is Resource.Error -> { Assert.fail("Loading must be first") }
                is Resource.Success -> { Assert.fail("Loading must be first") }
            }
        }
    }
}