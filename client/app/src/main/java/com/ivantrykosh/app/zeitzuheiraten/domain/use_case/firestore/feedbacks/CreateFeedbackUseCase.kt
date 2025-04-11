package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.feedbacks

import com.ivantrykosh.app.zeitzuheiraten.domain.model.Feedback
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.FeedbackRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserAuthRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import javax.inject.Inject

class CreateFeedbackUseCase @Inject constructor(
    private val userAuthRepository: UserAuthRepository,
    private val userRepository: UserRepository,
    private val feedbackRepository: FeedbackRepository,
) {
    operator fun invoke(postId: String, rating: Int, description: String) = flow<Resource<Unit>> {
        try {
            emit(Resource.Loading())
            val userId = userAuthRepository.getCurrentUserId()
            val user = userRepository.getUserById(userId)
            val date = LocalDateTime.now(ZoneId.of("UTC")).toInstant(ZoneOffset.UTC).toEpochMilli()
            val feedback = Feedback(
                userId = userId,
                username = user.name,
                postId = postId,
                rating = rating,
                description = description,
                date = date
            )
            feedbackRepository.createFeedback(feedback)
            emit(Resource.Success())
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }
}