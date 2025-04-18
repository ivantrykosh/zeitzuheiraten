package com.ivantrykosh.app.zeitzuheiraten.presenter.main.customer.budget_picker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivantrykosh.app.zeitzuheiraten.domain.model.PostWithRating
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.posts.GetPostsByBudgetUseCase
import com.ivantrykosh.app.zeitzuheiraten.utils.CategoryAndWeight
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import com.ivantrykosh.app.zeitzuheiraten.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

// todo split into two different view model for corresponding screens
@HiltViewModel
class BudgetPickerViewModel @Inject constructor(
    private val getPostsByBudgetUseCase: GetPostsByBudgetUseCase,
) : ViewModel() {

    var getPosts = MutableStateFlow(State<List<PostWithRating>>())
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

    fun updateBudgetAndGetPosts(category: String) {
        anyNewPosts = true
        viewModelScope.launch {
            getPostsByBudgetUseCase.updateBudget(budget.value.toInt(), chosenCategories.value)
            getPostsByBudgetUseCase(category, city.value, false, pageSize).collect { result ->
                getPosts.value = when (result) {
                    is Resource.Error -> State(error = result.error)
                    is Resource.Loading -> State(loading = true)
                    is Resource.Success -> {
                        if (result.data!!.size < pageSize) {
                            anyNewPosts = false
                        }
                        lastPosts.value = result.data
                        State(data = lastPosts.value)
                    }
                }
            }
        }
    }

    fun getPosts(category: String) {
        anyNewPosts = true
        getPostsByBudgetUseCase(category, city.value, false, pageSize).onEach { result ->
            getPosts.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> {
                    if (result.data!!.size < pageSize) {
                        anyNewPosts = false
                    }
                    lastPosts.value = result.data
                    State(data = lastPosts.value)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getNewPosts(category: String) {
        getPostsByBudgetUseCase(category, city.value, true, pageSize).onEach { result ->
            getPosts.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> {
                    if (result.data!!.size < pageSize) {
                        anyNewPosts = false
                    }
                    lastPosts.value = lastPosts.value.plus(result.data)
                    State(data = lastPosts.value)
                }
            }
        }.launchIn(viewModelScope)
    }
}