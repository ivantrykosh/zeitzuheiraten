package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.posts

import android.util.Log
import com.ivantrykosh.app.zeitzuheiraten.domain.model.PostWithRating
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.PostRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserAuthRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

private const val LOG_TAG = "GetPostsForCurrentUserUseCase"

class GetPostsForCurrentUserUseCase @Inject constructor(
    private val userAuthRepository: UserAuthRepository,
    private val postRepository: PostRepository,
) {
    operator fun invoke() = flow<Resource<List<PostWithRating>>> {
        try {
            emit(Resource.Loading())
            val userId = userAuthRepository.getCurrentUserId()
            val posts = postRepository.getPostsByUserId(userId)
            emit(Resource.Success(posts))
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message ?: "An error occurred")
            emit(Resource.Error(e))
        }
    }
}