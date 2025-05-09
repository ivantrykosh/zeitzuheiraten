package com.ivantrykosh.app.zeitzuheiraten.presenter.splash_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.users.GetCurrentUserUseCase
import com.ivantrykosh.app.zeitzuheiraten.presenter.clearState
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import com.ivantrykosh.app.zeitzuheiraten.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    var isUserLoggedInState = MutableStateFlow(State<Boolean>())
        private set

    var isUserProvider: Boolean = false
        private set

    fun clearIsUserLoggedInState() = clearState(isUserLoggedInState)

    fun isUserLoggedIn() {
        getCurrentUserUseCase().onEach { result ->
            isUserLoggedInState.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> {
                    val isLoggedIn = result.data != null
                    isUserProvider = result.data?.isProvider == true
                    State(data = isLoggedIn)
                }
            }
        }.launchIn(viewModelScope)
    }
}