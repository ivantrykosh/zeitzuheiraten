package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.users

import com.ivantrykosh.app.zeitzuheiraten.domain.repository.FeedbackRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.FirebaseStorageRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.PostRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserAuthRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteUserUseCase @Inject constructor(
    private val userAuthRepository: UserAuthRepository,
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
    private val feedbackRepository: FeedbackRepository,
    private val firebaseStorageRepository: FirebaseStorageRepository,
) {
    /**
     * Deletes user from Auth, Storage and Firestore
     */
    operator fun invoke() = flow<Resource<Unit>> {
        try {
            emit(Resource.Loading())
            val userId = userAuthRepository.getCurrentUserId()
            firebaseStorageRepository.deleteFolder(userId)
            val posts = postRepository.getPostsByUserId(userId)
            posts.forEach {
                postRepository.deletePost(it.id)
                feedbackRepository.deleteFeedbacksForPost(it.id)
            }
            feedbackRepository.deleteFeedbacksForUser(userId)
            userRepository.deleteUser(userId)
            userAuthRepository.deleteCurrentUser()
            emit(Resource.Success())
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }
}