package com.ivantrykosh.app.zeitzuheiraten.data.repository

import com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.firestore.FirestoreFeedbacks
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Feedback
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.FeedbackRepository
import javax.inject.Inject

class FeedbackRepositoryImpl @Inject constructor(
    private val firestoreFeedbacks: FirestoreFeedbacks
) : FeedbackRepository {
    override suspend fun createFeedback(feedback: Feedback) {
        firestoreFeedbacks.createFeedback(feedback)
    }

    override suspend fun getFeedbacksForPost(postId: String, startAfterLast: Boolean, pageSize: Int): List<Feedback> {
        return firestoreFeedbacks.getFeedbacksForPost(postId, startAfterLast, pageSize)
    }

    override suspend fun getFeedbacksForUser(userId: String, startAfterLast: Boolean, pageSize: Int): List<Feedback> {
        return firestoreFeedbacks.getFeedbacksForUser(userId, startAfterLast, pageSize)
    }

    override suspend fun deleteFeedback(id: String) {
        firestoreFeedbacks.deleteFeedback(id)
    }
}