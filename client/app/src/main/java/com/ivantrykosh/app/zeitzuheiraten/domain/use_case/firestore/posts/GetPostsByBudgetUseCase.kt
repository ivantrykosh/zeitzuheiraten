package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.posts

import android.content.Context
import com.ivantrykosh.app.zeitzuheiraten.domain.model.PostWithRating
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.FeedbackRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.PostRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.CategoryAndWeight
import com.ivantrykosh.app.zeitzuheiraten.utils.PricesBasedOnBudget
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetPostsByBudgetUseCase @Inject constructor(
    private val postRepository: PostRepository,
    @ApplicationContext private val context: Context,
    private val feedbackRepository: FeedbackRepository,
) {
    private lateinit var pricesBasedOnBudget: PricesBasedOnBudget

    fun updateBudget(budget: Int, categoriesAndWeights: List<CategoryAndWeight>) {
        pricesBasedOnBudget = PricesBasedOnBudget(context, budget, categoriesAndWeights)
    }

    operator fun invoke(category: String, city: String, startAfterLast: Boolean, pageSize: Int) = flow<Resource<List<PostWithRating>>> {
        try {
            emit(Resource.Loading())
            val deviation = 0.1f
            val price = pricesBasedOnBudget.optimalPrices[category]!!
            val posts = postRepository.getPostByFilters(category, city, (price - price * deviation).toInt(), (price + price * deviation).toInt(), startAfterLast, pageSize)
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