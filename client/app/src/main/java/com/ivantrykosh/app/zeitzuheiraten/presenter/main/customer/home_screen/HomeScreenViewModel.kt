package com.ivantrykosh.app.zeitzuheiraten.presenter.main.customer.home_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Post
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.posts.GetPostsByFiltersUseCase
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import com.ivantrykosh.app.zeitzuheiraten.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val getPostsByFiltersUseCase: GetPostsByFiltersUseCase,
) : ViewModel() {

    var getPosts = MutableStateFlow(State<List<Post>>())
        private set

    var lastPosts = MutableStateFlow(emptyList<Post>())
        private set

    var anyNewPosts: Boolean = true
        private set

    var lastCategory: String = ""
        private set
    var lastCity: String = ""
        private set
    var lastMaxPrice: Int? = null
        private set

    private var pageSize = 10

    init {
        getPostsByFilters(lastCategory, lastCity, lastMaxPrice)
    }

    fun getPostsByFilters(category: String, city: String, maxPrice: Int?) {
        lastCategory = category
        lastCity = city
        lastMaxPrice = maxPrice
        anyNewPosts = true
        getPostsByFiltersUseCase(category, city, maxPrice, false, pageSize).onEach { result ->
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

    fun getNewPostsByFilters(category: String, city: String, maxPrice: Int?) {
        getPostsByFiltersUseCase(category, city, maxPrice, true, pageSize).onEach { result ->
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