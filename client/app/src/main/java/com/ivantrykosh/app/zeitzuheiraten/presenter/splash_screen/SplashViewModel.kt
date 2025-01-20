package com.ivantrykosh.app.zeitzuheiraten.presenter.splash_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.auth.GetCurrentUserIdUseCase
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import com.ivantrykosh.app.zeitzuheiraten.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase
) : ViewModel() {

    var isUserLoggedInState = MutableStateFlow(State<Boolean>())
        private set

    fun isUserLoggedIn() {
        getCurrentUserIdUseCase().onEach { result ->
            isUserLoggedInState.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> {
                    val isLoggedIn = result.data!!.isNotEmpty()
                    State(data = isLoggedIn)
                }
            }
        }.launchIn(viewModelScope)
    }
}