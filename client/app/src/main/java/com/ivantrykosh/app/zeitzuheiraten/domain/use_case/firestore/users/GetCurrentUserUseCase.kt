package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.users

import android.util.Log
import com.ivantrykosh.app.zeitzuheiraten.domain.model.User
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserAuthRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

private const val LOG_TAG = "GetCurrentUserUseCase"

class GetCurrentUserUseCase @Inject constructor(
    private val userAuthRepository: UserAuthRepository,
    private val userRepository: UserRepository,
) {
    operator fun invoke() = flow<Resource<User?>> {
        try {
            emit(Resource.Loading())
            val userId = userAuthRepository.getCurrentUserId()
            if (userId.isNotEmpty()) {
                val user = userRepository.getUserById(userId)
                emit(Resource.Success(user))
            } else {
                emit(Resource.Success(null))
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message ?: "An error occurred")
            emit(Resource.Error(e))
        }
    }
}