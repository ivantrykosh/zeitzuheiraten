package com.ivantrykosh.app.zeitzuheiraten.domain.repository

import com.ivantrykosh.app.zeitzuheiraten.domain.model.Feedback
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Rating

interface FeedbackRepository {

    suspend fun createFeedback(feedback: Feedback)

    // todo use this method in all GetPost...UseCases
    suspend fun getRatingForPost(postId: String): Rating

    suspend fun getFeedbacksForPost(postId: String, startAfterLast: Boolean, pageSize: Int): List<Feedback>

    suspend fun getFeedbacksForUser(userId: String, startAfterLast: Boolean, pageSize: Int): List<Feedback>

    suspend fun deleteFeedback(id: String)

    suspend fun deleteFeedbacksForPost(postId: String)

    suspend fun deleteFeedbacksForUser(userId: String)
}