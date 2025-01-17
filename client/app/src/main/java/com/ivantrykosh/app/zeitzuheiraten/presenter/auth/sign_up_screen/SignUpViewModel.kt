package com.ivantrykosh.app.zeitzuheiraten.presenter.auth.sign_up_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivantrykosh.app.zeitzuheiraten.domain.model.User
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.users.CreateUserUseCase
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import com.ivantrykosh.app.zeitzuheiraten.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val createUserUseCase: CreateUserUseCase,
) : ViewModel() {

    var createUserState = MutableStateFlow(State<Unit>())
        private set

    fun createUser(email: String, password: String, name: String, isProvider: Boolean) {
        val user = User(
            name = name,
            email = email,
            isProvider = isProvider,
        )
        createUserUseCase(email, password, user).onEach { result ->
            createUserState.value = when (result) {
                is Resource.Error -> State(error = result.message)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> State(data = Unit)
            }
        }.launchIn(viewModelScope)
    }

    private val VALID_EMAIL_REGEX_PATTERN = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,10}$"
    fun isEmailValid(email: String): Boolean {
        val regex = Regex(VALID_EMAIL_REGEX_PATTERN, RegexOption.IGNORE_CASE)
        return email.matches(regex)
    }

    private val VALID_PASSWORD_REGEX_PATTERN = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,64}"
    fun isPasswordValid(password: String): Boolean {
        val regex = Regex(VALID_PASSWORD_REGEX_PATTERN)
        return password.matches(regex)
    }
}