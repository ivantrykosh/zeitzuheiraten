package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.auth

import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserAuthRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val userAuthRepository: UserAuthRepository
) {
    operator fun invoke(email: String, password: String) = flow<Resource<String>> {
        try {
            emit(Resource.Loading())
            userAuthRepository.signIn(email, password)
            val userId = userAuthRepository.getCurrentUserId()
            emit(Resource.Success(userId))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred after trying to sign in"))
        }
    }
}