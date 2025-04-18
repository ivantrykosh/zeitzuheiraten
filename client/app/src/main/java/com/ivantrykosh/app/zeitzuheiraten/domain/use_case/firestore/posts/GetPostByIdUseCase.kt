package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.posts

import com.ivantrykosh.app.zeitzuheiraten.domain.model.PostWithRating
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.FeedbackRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.PostRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetPostByIdUseCase @Inject constructor(
    private val postRepository: PostRepository,
    private val feedbackRepository: FeedbackRepository,
) {
    operator fun invoke(id: String) = flow<Resource<PostWithRating>> {
        try {
            emit(Resource.Loading())
            val post = postRepository.getPostById(id)
            val postWithRating = PostWithRating(
                id = post.id,
                providerId = post.providerId,
                providerName = post.providerName,
                category = post.category,
                cities = post.cities,
                description = post.description,
                minPrice = post.minPrice,
                photosUrl = post.photosUrl,
                notAvailableDates = post.notAvailableDates,
                rating = feedbackRepository.getRatingForPost(post.id)
            )
            emit(Resource.Success(postWithRating))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }
}