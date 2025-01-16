package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.users

import com.ivantrykosh.app.zeitzuheiraten.domain.model.User
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserAuthRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreateUserUseCase @Inject constructor(
    private val userAuthRepository: UserAuthRepository,
    private val userRepository: UserRepository,
) {
    /**
     * Sign up user in Auth and Storage. If error occurred deletes user for Auth if user exists
     */
    operator fun invoke(email: String, password: String, user: User) = flow<Resource<Unit>> {
        try {
            emit(Resource.Loading())
            userAuthRepository.signUp(email, password)
            val userId = userAuthRepository.getCurrentUserId()
            userRepository.createUser(user.copy(id = userId))
            emit(Resource.Success())
        } catch (e: Exception) {
            if (userAuthRepository.getCurrentUserId().isNotEmpty()) {
                userAuthRepository.deleteCurrentUser()
            }
            emit(Resource.Error(e.message ?: "An error occurred after trying to sign up"))
        }
    }
}