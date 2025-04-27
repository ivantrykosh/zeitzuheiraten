package com.ivantrykosh.app.zeitzuheiraten.presenter.main.shared.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivantrykosh.app.zeitzuheiraten.domain.model.User
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.reports.CreateReportUseCase
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.users.GetCurrentUserUseCase
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.users.GetUserByIdUseCase
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import com.ivantrykosh.app.zeitzuheiraten.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val createReportUseCase: CreateReportUseCase,
) : ViewModel() {

    var getCurrentUser = MutableStateFlow(State<User>())
        private set

    var getUserByIdState = MutableStateFlow(State<User>())
        private set

    var createReportState = MutableStateFlow(State<Unit>())
        private set

    fun getCurrentUser() {
        getCurrentUserUseCase().onEach { result ->
            getCurrentUser.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> {
                    State(data = result.data!!)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getUser(userId: String) {
        getUserByIdUseCase(userId).onEach { result ->
            getUserByIdState.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> {
                    State(data = result.data!!)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun createReport(userId: String, description: String) {
        createReportUseCase(userId, description).onEach { result ->
            createReportState.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> {
                    State(data = Unit)
                }
            }
        }.launchIn(viewModelScope)
    }
}