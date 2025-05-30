package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.users

import android.net.Uri
import android.util.Log
import com.ivantrykosh.app.zeitzuheiraten.domain.model.User
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.FirebaseStorageRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

private const val LOG_TAG = "UpdateUserProfileImageUseCase"

class UpdateUserProfileImageUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val firebaseStorageRepository: FirebaseStorageRepository,
) {
    /**
     * Updates user profile image. If imageUri is empty deletes image from Storage and updates user, otherwise uploads new image and updates user
     */
    operator fun invoke(user: User, imageUri: Uri, imageName: String) = flow<Resource<Unit>> {
        try {
            emit(Resource.Loading())
            val name = "${user.id}/$imageName"
            if (imageUri == Uri.EMPTY && user.imageUrl.isNotEmpty()) {
                firebaseStorageRepository.deleteImage(name)
                userRepository.updateUser(user.copy(imageUrl = ""))
            } else {
                val imageDownloadUrl = firebaseStorageRepository.uploadImage(name, imageUri)
                userRepository.updateUser(user.copy(imageUrl = imageDownloadUrl))
            }
            emit(Resource.Success())
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message ?: "An error occurred")
            emit(Resource.Error(e))
        }
    }
}