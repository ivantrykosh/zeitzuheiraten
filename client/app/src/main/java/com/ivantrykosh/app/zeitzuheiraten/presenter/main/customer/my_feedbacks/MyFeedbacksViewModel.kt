package com.ivantrykosh.app.zeitzuheiraten.presenter.main.customer.my_feedbacks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Feedback
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.feedbacks.DeleteFeedbackUseCase
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.feedbacks.GetFeedbacksForCurrentUserUseCase
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import com.ivantrykosh.app.zeitzuheiraten.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MyFeedbacksViewModel @Inject constructor(
    private val getFeedbacksForUserUseCase: GetFeedbacksForCurrentUserUseCase,
    private val deleteFeedbackUseCase: DeleteFeedbackUseCase,
) : ViewModel() {

    var getFeedbacksState = MutableStateFlow(State<Unit>())
        private set

    var deleteFeedbackState = MutableStateFlow(State<Unit>())
        private set

    var lastFeedbacks = MutableStateFlow(emptyList<Feedback>())
        private set

    var anyNewFeedbacks: Boolean = true
        private set

    private var pageSize = 10

    fun clearDeleteFeedbackState() {
        deleteFeedbackState.value = State()
    }

    fun getFeedbacks() {
        anyNewFeedbacks = true
        getFeedbacksForUserUseCase(false, pageSize).onEach { result ->
            getFeedbacksState.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> {
                    if (result.data!!.size < pageSize) {
                        anyNewFeedbacks = false
                    }
                    lastFeedbacks.value = result.data
                    State(data = Unit)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getNewFeedbacks() {
        getFeedbacksForUserUseCase(true, pageSize).onEach { result ->
            getFeedbacksState.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> {
                    if (result.data!!.size < pageSize) {
                        anyNewFeedbacks = false
                    }
                    lastFeedbacks.value = lastFeedbacks.value.plus(result.data)
                    State(data = Unit)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun deleteFeedback(id: String) {
        deleteFeedbackUseCase(id).onEach { result ->
            deleteFeedbackState.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> State(data = Unit)
            }
        }.launchIn(viewModelScope)
    }
}