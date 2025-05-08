package com.ivantrykosh.app.zeitzuheiraten.presenter.main.customer.budget_picker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivantrykosh.app.zeitzuheiraten.domain.model.PostWithRating
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.posts.GetPostsByBudgetUseCase
import com.ivantrykosh.app.zeitzuheiraten.presenter.loadPaginatedData
import com.ivantrykosh.app.zeitzuheiraten.utils.CategoryAndWeight
import com.ivantrykosh.app.zeitzuheiraten.utils.PostsOrderType
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import com.ivantrykosh.app.zeitzuheiraten.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BudgetPickerViewModel @Inject constructor(
    private val getPostsByBudgetUseCase: GetPostsByBudgetUseCase,
) : ViewModel() {

    var getPosts = MutableStateFlow(State<Unit>())
        private set

    var lastPosts = MutableStateFlow(emptyList<PostWithRating>())
        private set

    var anyNewPosts: Boolean = true
        private set

    private var pageSize = 10

    var city = MutableStateFlow("")
        private set

    var budget = MutableStateFlow("")
        private set

    val chosenCategories = MutableStateFlow(listOf<CategoryAndWeight>())
    val categories
        get() = chosenCategories.value.map { it.category }

    var lastPostsOrderType: PostsOrderType = PostsOrderType.BY_CATEGORY
        private set

    fun updateCity(city: String) {
        this.city.value = city
    }

    fun updateBudget(budget: String) {
        this.budget.value = budget
    }

    fun removeCategory(category: CategoryAndWeight) {
        chosenCategories.value = chosenCategories.value.minus(category)
    }

    fun addCategory(category: CategoryAndWeight) {
        chosenCategories.value = chosenCategories.value.plus(category)
    }

    fun updateBudgetAndGetPosts(category: String, postsOrderType: PostsOrderType) {
        anyNewPosts = true
        viewModelScope.launch {
            getPostsByBudgetUseCase.updateBudget(budget.value.toInt(), chosenCategories.value)
            getPostsByBudgetUseCase(category, city.value, false, pageSize, postsOrderType).collect { result ->
                getPosts.value = when (result) {
                    is Resource.Error -> State(error = result.error)
                    is Resource.Loading -> State(loading = true)
                    is Resource.Success -> {
                        if (result.data!!.size < pageSize) {
                            anyNewPosts = false
                        }
                        lastPosts.value = result.data
                        State(data = Unit)
                    }
                }
            }
        }
    }

    fun getPosts(category: String, postsOrderType: PostsOrderType, reset: Boolean) {
        lastPostsOrderType = postsOrderType
        loadPaginatedData(
            reset = reset,
            pageSize = pageSize,
            anyNewItems = { anyNewPosts = it },
            stateFlow = getPosts,
            resultFlow = lastPosts,
            useCaseCall = { size -> getPostsByBudgetUseCase(category, city.value, !reset, size, postsOrderType) }
        )
    }
}