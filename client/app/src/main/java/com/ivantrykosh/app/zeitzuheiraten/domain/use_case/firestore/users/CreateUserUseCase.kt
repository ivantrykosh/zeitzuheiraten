package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.users

import android.net.Uri
import android.util.Log
import com.ivantrykosh.app.zeitzuheiraten.domain.model.User
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.FirebaseStorageRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserAuthRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import java.time.Instant
import javax.inject.Inject

private const val LOG_TAG = "CreateUserUseCase"

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

            val dateTime = Instant.now().toEpochMilli()
            userRepository.createUser(user.copy(id = userId, imageUrl = imageDownloadUrl, creationTime = dateTime, lastUsernameChange = dateTime))
            emit(Resource.Success())
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message ?: "An error occurred")
            try {
                val userId = userAuthRepository.getCurrentUserId()
                if (userId.isNotEmpty()) {
                    userAuthRepository.deleteCurrentUser()
                    if (imageDownloadUrl.isNotEmpty()) {
                        firebaseStorageRepository.deleteImage("$userId/$imageName")
                    }
                }
            } catch (exception: Exception) {
                Log.e(LOG_TAG, exception.message ?: "An error occurred")
            }
            emit(Resource.Error(e))
        }
    }
}