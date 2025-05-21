package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.posts

import android.content.Context
import android.util.Log
import com.ivantrykosh.app.zeitzuheiraten.domain.model.PostWithRating
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.PostRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.CategoryAndWeight
import com.ivantrykosh.app.zeitzuheiraten.utils.PostsOrderType
import com.ivantrykosh.app.zeitzuheiraten.utils.PricesBasedOnBudget
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

private const val LOG_TAG = "GetPostsByBudgetUseCase"

class GetPostsByBudgetUseCase @Inject constructor(
    private val postRepository: PostRepository,
    @ApplicationContext private val context: Context,
) {
    private lateinit var pricesBasedOnBudget: PricesBasedOnBudget

    fun updateBudget(budget: Int, categoriesAndWeights: List<CategoryAndWeight>) {
        pricesBasedOnBudget = PricesBasedOnBudget(context, budget, categoriesAndWeights)
    }

    operator fun invoke(category: String, city: String, startAfterLast: Boolean, pageSize: Int, postsOrderType: PostsOrderType) = flow<Resource<List<PostWithRating>>> {
        try {
            emit(Resource.Loading())
            val deviation = 0.1f
            val price = pricesBasedOnBudget.optimalPrices[category]!!
            val deviationForPrice = (price * deviation).coerceAtLeast(1f).toInt()
            val posts = postRepository.getPostsByFilters(category, city, price - deviationForPrice, price + deviationForPrice, startAfterLast, pageSize, postsOrderType)
            emit(Resource.Success(posts))
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message ?: "An error occurred")
            emit(Resource.Error(e))
        }
    }
}