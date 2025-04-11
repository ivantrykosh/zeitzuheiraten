package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.feedbacks

import com.ivantrykosh.app.zeitzuheiraten.domain.model.Feedback
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.FeedbackRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetFeedbacksForPost @Inject constructor(
    private val feedbackRepository: FeedbackRepository,
) {
    operator fun invoke(postId: String, startAfterLast: Boolean, pageSize: Int) = flow<Resource<List<Feedback>>> {
        try {
            emit(Resource.Loading())
            val feedbacks = feedbackRepository.getFeedbacksForPost(postId, startAfterLast, pageSize)
            emit(Resource.Success(feedbacks))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }
}