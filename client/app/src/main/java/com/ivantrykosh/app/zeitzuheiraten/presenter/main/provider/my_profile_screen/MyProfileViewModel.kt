package com.ivantrykosh.app.zeitzuheiraten.presenter.main.provider.my_profile_screen

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivantrykosh.app.zeitzuheiraten.domain.model.User
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.auth.ReAuthenticateUseCase
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.auth.SignOutUseCase
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.users.DeleteUserUseCase
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.users.GetCurrentUserUseCase
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.users.UpdateUserProfileImageUseCase
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.users.UpdateUserUseCase
import com.ivantrykosh.app.zeitzuheiraten.utils.Constants.USER_PROFILE_PICTURE_NAME
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import com.ivantrykosh.app.zeitzuheiraten.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MyProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val updateUserProfileImageUseCase: UpdateUserProfileImageUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val reAuthenticateUseCase: ReAuthenticateUseCase,
) : ViewModel() {

    var getCurrentUserState = MutableStateFlow(State<User>())
        private set

    var updateUserState = MutableStateFlow(State<Unit>())
        private set

    var updateUserProfileImageState = MutableStateFlow(State<Unit>())
        private set

    var deleteUserState = MutableStateFlow(State<Unit>())
        private set

    var signOutState = MutableStateFlow(State<Unit>())
        private set

    var reAuthenticateState = MutableStateFlow(State<Unit>())
        private set

    fun getCurrentUser() {
        getCurrentUserUseCase().onEach { result ->
            getCurrentUserState.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> State(data = result.data!!)
            }
        }.launchIn(viewModelScope)
    }

    fun updateUser(user: User) {
        updateUserUseCase(user).onEach { result ->
            updateUserState.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> State(data = Unit)
            }
        }.launchIn(viewModelScope)
    }

    fun updateUserProfileImage(imageUri: Uri) {
        val user = getCurrentUserState.value.data!!
        updateUserProfileImageUseCase(user, imageUri, USER_PROFILE_PICTURE_NAME).onEach { result ->
            updateUserProfileImageState.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> State(data = Unit)
            }
        }.launchIn(viewModelScope)
    }

    fun deleteUser() {
        deleteUserUseCase().onEach { result ->
            deleteUserState.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> State(data = Unit)
            }
        }.launchIn(viewModelScope)
    }

    fun signOut() {
        signOutUseCase().onEach { result ->
            signOutState.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> State(data = Unit)
            }
        }.launchIn(viewModelScope)
    }

    fun reAuthenticate(password: String) {
        val userEmail = getCurrentUserState.value.data!!.email
        reAuthenticateUseCase(userEmail, password).onEach { result ->
            reAuthenticateState.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> State(data = Unit)
            }
        }.launchIn(viewModelScope)
    }
}