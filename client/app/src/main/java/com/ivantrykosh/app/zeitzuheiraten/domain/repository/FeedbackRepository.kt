package com.ivantrykosh.app.zeitzuheiraten.domain.repository

import com.ivantrykosh.app.zeitzuheiraten.domain.model.Feedback

interface FeedbackRepository {

    suspend fun createFeedback(feedback: Feedback)

    suspend fun getFeedbacksForPost(postId: String, startAfterLast: Boolean, pageSize: Int): List<Feedback>

    suspend fun getFeedbacksForUser(userId: String, startAfterLast: Boolean, pageSize: Int): List<Feedback>

    suspend fun deleteFeedback(id: String)
}