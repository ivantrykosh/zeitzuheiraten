package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.auth

import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserAuthRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SendVerificationEmailUseCase @Inject constructor(
    private val userAuthRepository: UserAuthRepository
) {
    operator fun invoke() = flow<Resource<Unit>> {
        try {
            emit(Resource.Loading())
            userAuthRepository.sendVerificationEmail()
            emit(Resource.Success())
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred after trying to send verification email"))
        }
    }
}