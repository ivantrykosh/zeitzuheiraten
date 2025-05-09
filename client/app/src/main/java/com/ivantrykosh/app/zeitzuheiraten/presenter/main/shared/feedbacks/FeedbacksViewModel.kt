package com.ivantrykosh.app.zeitzuheiraten.presenter.main.shared.feedbacks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Feedback
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.auth.GetCurrentUserIdUseCase
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.feedbacks.GetFeedbacksForPostUseCase
import com.ivantrykosh.app.zeitzuheiraten.presenter.clearState
import com.ivantrykosh.app.zeitzuheiraten.presenter.loadPaginatedData
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import com.ivantrykosh.app.zeitzuheiraten.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class FeedbacksViewModel @Inject constructor(
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val getFeedbacksForPostUseCase: GetFeedbacksForPostUseCase,
) : ViewModel() {

    var currentUserIdState = MutableStateFlow(State<String>())
        private set

    var getFeedbacksState = MutableStateFlow(State<Unit>())
        private set

    var lastFeedbacks = MutableStateFlow(emptyList<Feedback>())
        private set

    var anyNewFeedbacks: Boolean = true
        private set

    private var pageSize = 10

    init {
        getCurrentUserId()
    }

    private fun getCurrentUserId() {
        getCurrentUserIdUseCase().onEach { result ->
            currentUserIdState.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> State(data = result.data!!)
            }
        }.launchIn(viewModelScope)
    }

    fun clearGetFeedbacksState() = clearState(getFeedbacksState)

    fun getFeedbacks(postId: String, reset: Boolean) {
        loadPaginatedData(
            reset = reset,
            pageSize = pageSize,
            anyNewItems = { anyNewFeedbacks = it },
            stateFlow = getFeedbacksState,
            resultFlow = lastFeedbacks,
            useCaseCall = { size -> getFeedbacksForPostUseCase(postId, !reset, size) }
        )
    }
}