package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.feedbacks

import com.ivantrykosh.app.zeitzuheiraten.domain.model.Feedback
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.FeedbackRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserAuthRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetFeedbacksForCurrentUserUseCase @Inject constructor(
    private val userAuthRepository: UserAuthRepository,
    private val feedbackRepository: FeedbackRepository,
) {
    operator fun invoke(startAfterLast: Boolean, pageSize: Int) = flow<Resource<List<Feedback>>> {
        try {
            emit(Resource.Loading())
            val userId = userAuthRepository.getCurrentUserId()
            val feedbacks = feedbackRepository.getFeedbacksForUser(userId, startAfterLast, pageSize)
            emit(Resource.Success(feedbacks))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }
}