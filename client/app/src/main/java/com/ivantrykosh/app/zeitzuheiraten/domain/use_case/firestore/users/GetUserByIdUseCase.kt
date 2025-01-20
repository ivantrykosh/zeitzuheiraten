package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.users

import com.ivantrykosh.app.zeitzuheiraten.domain.model.User
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetUserByIdUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(userId: String) = flow<Resource<User>> {
        try {
            emit(Resource.Loading())
            val user = userRepository.getUserById(userId)
            emit(Resource.Success(user))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }
}