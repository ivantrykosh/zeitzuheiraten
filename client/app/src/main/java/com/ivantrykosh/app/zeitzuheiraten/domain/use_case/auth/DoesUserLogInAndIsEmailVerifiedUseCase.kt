package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.auth

import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserAuthRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DoesUserLogInAndIsEmailVerifiedUseCase @Inject constructor(
    private val userAuthRepository: UserAuthRepository
) {
    operator fun invoke() = flow<Resource<Boolean>> {
        try {
            emit(Resource.Loading())
            val userId = userAuthRepository.getCurrentUserId()
            if (userId.isEmpty()) {
                emit(Resource.Success(false))
            } else {
                val isEmailVerified = userAuthRepository.isEmailVerified()
                emit(Resource.Success(isEmailVerified))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred after trying to get user id and check if email is verified"))
        }
    }
}