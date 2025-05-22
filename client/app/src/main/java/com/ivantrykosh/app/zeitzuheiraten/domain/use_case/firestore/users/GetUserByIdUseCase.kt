package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.users

import android.util.Log
import com.ivantrykosh.app.zeitzuheiraten.domain.model.User
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

private const val LOG_TAG = "GetUserByIdUseCase"

class GetUserByIdUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    /**
     * Return user by id. If user does not exist error will be emitted
     */
    operator fun invoke(userId: String) = flow<Resource<User>> {
        try {
            emit(Resource.Loading())
            val user = userRepository.getUserById(userId)!!
            emit(Resource.Success(user))
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message ?: "An error occurred")
            emit(Resource.Error(e))
        }
    }
}