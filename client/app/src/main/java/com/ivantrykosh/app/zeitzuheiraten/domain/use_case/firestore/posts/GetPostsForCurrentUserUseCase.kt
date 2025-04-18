package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.posts

import com.ivantrykosh.app.zeitzuheiraten.domain.model.PostWithRating
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.FeedbackRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.PostRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserAuthRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetPostsForCurrentUserUseCase @Inject constructor(
    private val userAuthRepository: UserAuthRepository,
    private val postRepository: PostRepository,
    private val feedbackRepository: FeedbackRepository,
) {
    operator fun invoke() = flow<Resource<List<PostWithRating>>> {
        try {
            emit(Resource.Loading())
            val userId = userAuthRepository.getCurrentUserId()
            val posts = postRepository.getPostsByUserId(userId)
            val postsWithRating = posts.map {
                PostWithRating(
                    id = it.id,
                    providerId = it.providerId,
                    providerName = it.providerName,
                    category = it.category,
                    cities = it.cities,
                    description = it.description,
                    minPrice = it.minPrice,
                    photosUrl = it.photosUrl,
                    notAvailableDates = it.notAvailableDates,
                    rating = feedbackRepository.getRatingForPost(it.id)
                )
            }
            emit(Resource.Success(postsWithRating))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }
}