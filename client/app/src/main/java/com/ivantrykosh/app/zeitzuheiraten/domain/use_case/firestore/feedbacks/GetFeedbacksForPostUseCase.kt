package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.feedbacks

import android.util.Log
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Feedback
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.FeedbackRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

private const val LOG_TAG = "GetFeedbacksForPostUseCase"

class GetFeedbacksForPostUseCase @Inject constructor(
    private val feedbackRepository: FeedbackRepository,
) {
    operator fun invoke(postId: String, startAfterLast: Boolean, pageSize: Int) = flow<Resource<List<Feedback>>> {
        try {
            emit(Resource.Loading())
            val feedbacks = feedbackRepository.getFeedbacksForPost(postId, startAfterLast, pageSize)
            emit(Resource.Success(feedbacks))
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message ?: "An error occurred")
            emit(Resource.Error(e))
        }
    }
}