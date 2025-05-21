package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.feedbacks

import com.ivantrykosh.app.zeitzuheiraten.data.repository.FeedbackRepositoryImpl
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
class DeleteFeedbackUseCaseTest {

    private lateinit var feedbackRepositoryImpl: FeedbackRepositoryImpl
    private lateinit var deleteFeedbackUseCase: DeleteFeedbackUseCase

    @Before
    fun setup() {
        feedbackRepositoryImpl = mock()
        deleteFeedbackUseCase = DeleteFeedbackUseCase(feedbackRepositoryImpl)
    }

    @Test
    fun `delete feedback successfully`() = runBlocking {
        val feedbackId = "feedbackId"
        var resourceSuccess = false
        whenever(feedbackRepositoryImpl.deleteFeedback(feedbackId)).doReturn(Unit)

        deleteFeedbackUseCase(feedbackId).collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { Assert.fail(result.error.message) }
                is Resource.Success -> { resourceSuccess = true }
            }
        }

        verify(feedbackRepositoryImpl).deleteFeedback(feedbackId)
        Assert.assertTrue(resourceSuccess)
    }

    @Test(expected = CancellationException::class)
    fun `delete feedback first emit must be loading`() = runBlocking {
        val feedbackId = "feedbackId"

        deleteFeedbackUseCase(feedbackId).collect { result ->
            when (result) {
                is Resource.Loading -> { this.cancel() }
                is Resource.Error -> { Assert.fail("Loading must be first") }
                is Resource.Success -> { Assert.fail("Loading must be first") }
            }
        }
    }
}