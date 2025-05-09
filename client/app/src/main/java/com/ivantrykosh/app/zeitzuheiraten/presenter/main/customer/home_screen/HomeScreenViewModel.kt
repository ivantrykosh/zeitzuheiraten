package com.ivantrykosh.app.zeitzuheiraten.presenter.main.customer.home_screen

import androidx.lifecycle.ViewModel
import com.ivantrykosh.app.zeitzuheiraten.domain.model.PostWithRating
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.posts.GetPostsByFiltersUseCase
import com.ivantrykosh.app.zeitzuheiraten.presenter.clearState
import com.ivantrykosh.app.zeitzuheiraten.presenter.loadPaginatedData
import com.ivantrykosh.app.zeitzuheiraten.utils.PostsOrderType
import com.ivantrykosh.app.zeitzuheiraten.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val getPostsByFiltersUseCase: GetPostsByFiltersUseCase,
) : ViewModel() {

    var getPosts = MutableStateFlow(State<Unit>())
        private set

    var lastPosts = MutableStateFlow(emptyList<PostWithRating>())
        private set

    var anyNewPosts: Boolean = true
        private set

    var lastCategory: String = ""
        private set
    var lastCity: String = ""
        private set
    var lastMaxPrice: Int? = null
        private set
    var lastPostsOrderType: PostsOrderType = PostsOrderType.BY_CATEGORY
        private set

    private var pageSize = 10

    init {
        getPostsByFilters(lastCategory, lastCity, lastMaxPrice, lastPostsOrderType, reset = true)
    }

    fun clearGetPostsState() = clearState(getPosts)

    fun getPostsByFilters(category: String, city: String, maxPrice: Int?, postsOrderType: PostsOrderType, reset: Boolean) {
        lastCategory = category
        lastCity = city
        lastMaxPrice = maxPrice
        lastPostsOrderType = postsOrderType
        loadPaginatedData(
            reset = reset,
            pageSize = pageSize,
            anyNewItems = { anyNewPosts = it },
            stateFlow = getPosts,
            resultFlow = lastPosts,
            useCaseCall = { size -> getPostsByFiltersUseCase(category, city, maxPrice, !reset, size, postsOrderType) }
        )
    }
}