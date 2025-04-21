package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.posts

import com.ivantrykosh.app.zeitzuheiraten.domain.model.PostWithRating
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.PostRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.OrderType
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetPostsByFiltersUseCase @Inject constructor(
    private val postRepository: PostRepository,
) {
    operator fun invoke(category: String, city: String, maxPrice: Int?, startAfterLast: Boolean, pageSize: Int, orderType: OrderType) = flow<Resource<List<PostWithRating>>> {
        try {
            emit(Resource.Loading())
            val posts = postRepository.getPostByFilters(category, city, minPrice = null, maxPrice, startAfterLast, pageSize, orderType)
            emit(Resource.Success(posts))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }
}