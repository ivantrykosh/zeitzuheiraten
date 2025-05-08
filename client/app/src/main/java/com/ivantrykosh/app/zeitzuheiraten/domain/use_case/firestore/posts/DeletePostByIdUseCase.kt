package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.posts

import android.util.Log
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.PostRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

private const val LOG_TAG = "DeletePostByIdUseCase"

class DeletePostByIdUseCase @Inject constructor(
    private val postRepository: PostRepository,
) {
    operator fun invoke(id: String) = flow<Resource<Unit>> {
        try {
            emit(Resource.Loading())
            postRepository.deletePost(id)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message ?: "An error occurred")
            emit(Resource.Error(e))
        }
    }
}