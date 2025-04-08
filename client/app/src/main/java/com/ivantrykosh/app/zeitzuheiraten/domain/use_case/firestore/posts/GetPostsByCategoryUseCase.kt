package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.posts

import com.ivantrykosh.app.zeitzuheiraten.domain.model.Post
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.PostRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetPostsByCategoryUseCase @Inject constructor(
    private val postRepository: PostRepository,
) {
    operator fun invoke(category: String, pageIndex: Int, pageSize: Int) = flow<Resource<List<Post>>> {
        try {
            emit(Resource.Loading())
            val posts = postRepository.getPostByCategory(category, pageIndex, pageSize)
            emit(Resource.Success(posts))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }
}