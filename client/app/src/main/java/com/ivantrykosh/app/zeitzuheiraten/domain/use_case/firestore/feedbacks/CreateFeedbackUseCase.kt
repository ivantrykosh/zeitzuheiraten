package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.feedbacks

import android.util.Log
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Feedback
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.FeedbackRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserAuthRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import java.time.Instant
import javax.inject.Inject

private const val LOG_TAG = "CreateFeedbackUseCase"

class CreateFeedbackUseCase @Inject constructor(
    private val userAuthRepository: UserAuthRepository,
    private val userRepository: UserRepository,
    private val feedbackRepository: FeedbackRepository,
) {
    operator fun invoke(postId: String, category: String, provider: String, rating: Int, description: String) = flow<Resource<Unit>> {
        try {
            emit(Resource.Loading())
            val userId = userAuthRepository.getCurrentUserId()
            val user = userRepository.getUserById(userId)
            val date = Instant.now().toEpochMilli()
            val feedback = Feedback(
                userId = userId,
                username = user.name,
                postId = postId,
                category = category,
                provider = provider,
                rating = rating,
                description = description,
                date = date
            )
            feedbackRepository.createFeedback(feedback)
            emit(Resource.Success())
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message ?: "An error occurred")
            emit(Resource.Error(e))
        }
    }
}