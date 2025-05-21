package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.posts

import android.util.Log
import com.ivantrykosh.app.zeitzuheiraten.domain.model.PostWithRating
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.PostRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.PostsOrderType
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

private const val LOG_TAG = "GetPostsByFiltersUseCase"

class GetPostsByFiltersUseCase @Inject constructor(
    private val postRepository: PostRepository,
) {
    operator fun invoke(category: String, city: String, maxPrice: Int?, startAfterLast: Boolean, pageSize: Int, postsOrderType: PostsOrderType) = flow<Resource<List<PostWithRating>>> {
        try {
            emit(Resource.Loading())
            val posts = postRepository.getPostsByFilters(category, city, minPrice = null, maxPrice, startAfterLast, pageSize, postsOrderType)
            emit(Resource.Success(posts))
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message ?: "An error occurred")
            emit(Resource.Error(e))
        }
    }
}