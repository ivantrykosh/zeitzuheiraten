package com.ivantrykosh.app.zeitzuheiraten.presenter.splash_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.auth.DoesUserLogInAndIsEmailVerifiedUseCase
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import com.ivantrykosh.app.zeitzuheiraten.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val doesUserLogInAndIsEmailVerifiedUseCase: DoesUserLogInAndIsEmailVerifiedUseCase
) : ViewModel() {

    var isUserLoggedInAndIsEmailVerifiedState = MutableStateFlow(State<Boolean>())
        private set

    fun isUserLoggedIn() {
        doesUserLogInAndIsEmailVerifiedUseCase().onEach { result ->
            isUserLoggedInAndIsEmailVerifiedState.value = when (result) {
                is Resource.Error -> State(error = result.message)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> State(data = result.data!!)
            }
        }.launchIn(viewModelScope)
    }
}