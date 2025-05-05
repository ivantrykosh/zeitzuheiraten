package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.auth

import android.util.Log
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserAuthRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

private const val LOG_TAG = "IsEmailVerifiedUseCase"

class IsEmailVerifiedUseCase @Inject constructor(
    private val userAuthRepository: UserAuthRepository
) {
    operator fun invoke() = flow<Resource<Boolean>> {
        try {
            emit(Resource.Loading())
            val isVerified = userAuthRepository.isEmailVerified()
            emit(Resource.Success(isVerified))
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message ?: "An error occurred")
            emit(Resource.Error(e))
        }
    }
}