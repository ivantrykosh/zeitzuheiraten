package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.feedbacks

import android.util.Log
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.FeedbackRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

private const val LOG_TAG = "DeleteFeedbackUseCase"

class DeleteFeedbackUseCase @Inject constructor(
    private val feedbackRepository: FeedbackRepository,
) {
    operator fun invoke(id: String) = flow<Resource<Unit>> {
        try {
            emit(Resource.Loading())
            feedbackRepository.deleteFeedback(id)
            emit(Resource.Success())
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message ?: "An error occurred")
            emit(Resource.Error(e))
        }
    }
}