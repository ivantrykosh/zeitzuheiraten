package com.ivantrykosh.app.zeitzuheiraten.presenter.splash_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.users.GetCurrentUserUseCase
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.github.GetLatestAppVersionUseCase
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
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getLatestAppVersionUseCase: GetLatestAppVersionUseCase
) : ViewModel() {

    var isUserLoggedInState = MutableStateFlow(State<Boolean>())
        private set

    var isUserProvider: Boolean = false
        private set

    var getLatestAppVersionState = MutableStateFlow(State<String>())
        private set

    init {
        getLatestVersion()
    }

    fun clearIsUserLoggedInState() = clearState(isUserLoggedInState)

    fun clearGetLatestAppVersionState() = clearState(getLatestAppVersionState)

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

    fun getLatestVersion() {
        getLatestAppVersionUseCase().onEach { result ->
            getLatestAppVersionState.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> State(data = result.data)
            }
        }.launchIn(viewModelScope)
    }

    fun inNewerVersion(current: String, latest: String): Boolean {
        val cParts = current.trimStart('v').split(".").mapNotNull { it.toIntOrNull() }
        val lParts = latest.trimStart('v').split(".").mapNotNull { it.toIntOrNull() }

        for (i in 0 until maxOf(cParts.size, lParts.size)) {
            val c = cParts.getOrElse(i) { 0 }
            val l = lParts.getOrElse(i) { 0 }
            if (l > c) return true
            if (l < c) return false
        }
        return false
    }
}