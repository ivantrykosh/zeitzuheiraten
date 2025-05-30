package com.ivantrykosh.app.zeitzuheiraten.presenter.auth.sign_up_screen

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivantrykosh.app.zeitzuheiraten.domain.model.User
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.users.CreateUserUseCase
import com.ivantrykosh.app.zeitzuheiraten.presenter.clearState
import com.ivantrykosh.app.zeitzuheiraten.utils.Constants.USER_PROFILE_PICTURE_NAME
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

    fun clearCreateUserState() = clearState(createUserState)

    fun createUser(email: String, password: String, name: String, isProvider: Boolean, imageUri: Uri) {
        val user = User(
            name = name,
            email = email,
            isProvider = isProvider,
        )
        createUserUseCase(email, password, user, imageUri, USER_PROFILE_PICTURE_NAME).onEach { result ->
            createUserState.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> State(data = Unit)
            }
        }.launchIn(viewModelScope)
    }
}