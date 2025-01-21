package com.ivantrykosh.app.zeitzuheiraten.data.repository

import android.net.Uri
import com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.storage.FirebaseStorage
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.FirebaseStorageRepository
import javax.inject.Inject

class FirebaseStorageRepositoryImpl @Inject constructor(
    private val firebaseStorage: FirebaseStorage
) : FirebaseStorageRepository {
    override suspend fun uploadImage(name: String, imageUri: Uri): String {
        return firebaseStorage.uploadImage(name, imageUri)
    }

    override suspend fun deleteImageOrFolder(name: String) {
        firebaseStorage.deleteImageOrFolder(name)
    }
}