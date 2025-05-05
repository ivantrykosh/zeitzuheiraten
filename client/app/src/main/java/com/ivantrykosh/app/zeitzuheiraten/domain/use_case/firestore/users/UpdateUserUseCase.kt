package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.users

import android.util.Log
import com.ivantrykosh.app.zeitzuheiraten.domain.model.User
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

private const val LOG_TAG = "UpdateUserUseCase"

class UpdateUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(user: User) = flow<Resource<Unit>> {
        try {
            emit(Resource.Loading())
            userRepository.updateUser(user)
            emit(Resource.Success())
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message ?: "An error occurred")
            emit(Resource.Error(e))
        }
    }
}