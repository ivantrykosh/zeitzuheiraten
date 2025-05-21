package com.ivantrykosh.app.zeitzuheiraten.data.repository

import com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.firestore.FirestoreFeedbacks
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Feedback
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class FeedbackRepositoryImplTest {

    private lateinit var mockFirestoreFeedbacks: FirestoreFeedbacks
    private lateinit var feedbacksRepositoryImpl: FeedbackRepositoryImpl

    @Before
    fun setup() {
        mockFirestoreFeedbacks = mock()
        feedbacksRepositoryImpl = FeedbackRepositoryImpl(mockFirestoreFeedbacks)
    }

    @Test
    fun `create feedback successfully`() = runTest{
        val feedback = Feedback()
        whenever(mockFirestoreFeedbacks.createFeedback(feedback)).doReturn(Unit)

        feedbacksRepositoryImpl.createFeedback(feedback)

        verify(mockFirestoreFeedbacks).createFeedback(feedback)
    }

    @Test
    fun `get feedbacks for post successfully`() = runTest{
        val postId = "postId"
        val startAfterLast = false
        val pageSize = 10
        val expectedFeedbacks = listOf(Feedback(), Feedback())
        whenever(mockFirestoreFeedbacks.getFeedbacksForPost(postId, startAfterLast, pageSize)).doReturn(expectedFeedbacks)

        val actualFeedbacks = feedbacksRepositoryImpl.getFeedbacksForPost(postId, startAfterLast, pageSize)

        verify(mockFirestoreFeedbacks).getFeedbacksForPost(postId, startAfterLast, pageSize)
        assertEquals(expectedFeedbacks, actualFeedbacks)
    }

    @Test
    fun `get feedbacks for user successfully`() = runTest{
        val userId = "userId"
        val startAfterLast = false
        val pageSize = 10
        val expectedFeedbacks = listOf(Feedback(), Feedback())
        whenever(mockFirestoreFeedbacks.getFeedbacksForUser(userId, startAfterLast, pageSize)).doReturn(expectedFeedbacks)

        val actualFeedbacks = feedbacksRepositoryImpl.getFeedbacksForUser(userId, startAfterLast, pageSize)

        verify(mockFirestoreFeedbacks).getFeedbacksForUser(userId, startAfterLast, pageSize)
        assertEquals(expectedFeedbacks, actualFeedbacks)
    }

    @Test
    fun `delete feedback successfully`() = runTest{
        val feedbackId = "feedbackId"
        whenever(mockFirestoreFeedbacks.deleteFeedback(feedbackId)).doReturn(Unit)

        feedbacksRepositoryImpl.deleteFeedback(feedbackId)

        verify(mockFirestoreFeedbacks).deleteFeedback(feedbackId)
    }
}