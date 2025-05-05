package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.posts

import android.util.Log
import com.ivantrykosh.app.zeitzuheiraten.domain.model.PostWithRating
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.PostRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

private const val LOG_TAG = "GetPostByIdUseCase"

class GetPostByIdUseCase @Inject constructor(
    private val postRepository: PostRepository,
) {
    operator fun invoke(id: String) = flow<Resource<PostWithRating>> {
        try {
            emit(Resource.Loading())
            val post = postRepository.getPostById(id)
            emit(Resource.Success(post))
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message ?: "An error occurred")
            emit(Resource.Error(e))
        }
    }
}