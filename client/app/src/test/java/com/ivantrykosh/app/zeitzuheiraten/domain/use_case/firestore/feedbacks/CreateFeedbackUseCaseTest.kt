package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.feedbacks

import com.ivantrykosh.app.zeitzuheiraten.data.repository.FeedbackRepositoryImpl
import com.ivantrykosh.app.zeitzuheiraten.data.repository.UserAuthRepositoryImpl
import com.ivantrykosh.app.zeitzuheiraten.data.repository.UserRepositoryImpl
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
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class CreateFeedbackUseCaseTest {

    private lateinit var userAuthRepositoryImpl: UserAuthRepositoryImpl
    private lateinit var userRepositoryImpl: UserRepositoryImpl
    private lateinit var feedbackRepositoryImpl: FeedbackRepositoryImpl
    private lateinit var createFeedbackUseCase: CreateFeedbackUseCase

    @Before
    fun setup() {
        userAuthRepositoryImpl = mock()
        userRepositoryImpl = mock()
        feedbackRepositoryImpl = mock()
        createFeedbackUseCase = CreateFeedbackUseCase(userAuthRepositoryImpl, userRepositoryImpl, feedbackRepositoryImpl)
    }

    @Test
    fun `create feedback successfully`() = runBlocking {
        val postId = "postId"
        val category = "Video"
        val provider = "Provider Name"
        val rating = 5
        val description = "Test"
        val userId = "userId"
        val user = User(id = userId, name = "Test User", email = "test@email.com")
        var resourceSuccess = false
        whenever(userAuthRepositoryImpl.getCurrentUserId()).doReturn(userId)
        whenever(userRepositoryImpl.getUserById(userId)).doReturn(user)
        whenever(feedbackRepositoryImpl.createFeedback(any())).doReturn(Unit)

        createFeedbackUseCase(postId, category, provider, rating, description).collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { Assert.fail(result.error.message) }
                is Resource.Success -> { resourceSuccess = true }
            }
        }

        verify(userAuthRepositoryImpl).getCurrentUserId()
        verify(userRepositoryImpl).getUserById(userId)
        verify(feedbackRepositoryImpl).createFeedback(any())
        Assert.assertTrue(resourceSuccess)
    }

    @Test(expected = CancellationException::class)
    fun `create feedback first emit must be loading`() = runBlocking {
        val postId = "postId"
        val category = "Video"
        val provider = "Provider Name"
        val rating = 5
        val description = "Test"

        createFeedbackUseCase(postId, category, provider, rating, description).collect { result ->
            when (result) {
                is Resource.Loading -> { this.cancel() }
                is Resource.Error -> { Assert.fail("Loading must be first") }
                is Resource.Success -> { Assert.fail("Loading must be first") }
            }
        }
    }
}