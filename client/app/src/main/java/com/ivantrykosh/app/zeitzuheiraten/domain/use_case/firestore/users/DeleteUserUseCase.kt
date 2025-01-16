package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.users

import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserAuthRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteUserUseCase @Inject constructor(
    private val userAuthRepository: UserAuthRepository,
    private val userRepository: UserRepository,
) {
    /**
     * Deletes user from Auth and Storage
     */
    operator fun invoke() = flow<Resource<Unit>> {
        try {
            emit(Resource.Loading())
            val userId = userAuthRepository.getCurrentUserId()
            userAuthRepository.deleteCurrentUser()
            userRepository.deleteUser(userId)
            emit(Resource.Success())
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred after trying to delete user"))
        }
    }
}