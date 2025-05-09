package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.auth

import android.util.Log
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserAuthRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

private const val LOG_TAG = "SignInUseCase"

class SignInUseCase @Inject constructor(
    private val userAuthRepository: UserAuthRepository,
    private val userRepository: UserRepository,
) {
    operator fun invoke(email: String, password: String) = flow<Resource<Boolean>> {
        try {
            emit(Resource.Loading())
            userAuthRepository.signIn(email, password)
            val userId = userAuthRepository.getCurrentUserId()
            val isProvider = userRepository.getUserById(userId)!!.isProvider
            emit(Resource.Success(isProvider))
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message ?: "An error occurred")
            emit(Resource.Error(e))
        }
    }
}