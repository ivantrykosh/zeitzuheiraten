package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.users

import android.net.Uri
import com.ivantrykosh.app.zeitzuheiraten.domain.model.User
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.FirebaseStorageRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserAuthRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreateUserUseCase @Inject constructor(
    private val userAuthRepository: UserAuthRepository,
    private val userRepository: UserRepository,
    private val firebaseStorageRepository: FirebaseStorageRepository,
) {
    /**
     * Signs up user in Auth and Firestore. If error occurred deletes user if user exists. Also uploads user image to Storage in folder userid/.
     */
    operator fun invoke(email: String, password: String, user: User, imageUri: Uri = Uri.EMPTY, imageName: String = "") = flow<Resource<Unit>> {
        var imageDownloadUrl = ""
        try {
            emit(Resource.Loading())
            userAuthRepository.signUp(email, password)
            val userId = userAuthRepository.getCurrentUserId()

            if (imageUri != Uri.EMPTY) {
                val name = "$userId/$imageName"
                imageDownloadUrl = firebaseStorageRepository.uploadImage(name, imageUri)
            }

            userRepository.createUser(user.copy(id = userId, imageUrl = imageDownloadUrl))
            emit(Resource.Success())
        } catch (e: Exception) {
            val userId = userAuthRepository.getCurrentUserId()
            if (userId.isNotEmpty()) {
                userAuthRepository.deleteCurrentUser()
                if (imageDownloadUrl.isNotEmpty()) {
                    firebaseStorageRepository.deleteImage("$userId/$imageName")
                }
            }
            emit(Resource.Error(e))
        }
    }
}