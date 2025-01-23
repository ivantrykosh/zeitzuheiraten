package com.ivantrykosh.app.zeitzuheiraten.domain.repository

import android.net.Uri

interface FirebaseStorageRepository {

    suspend fun uploadImage(name: String, imageUri: Uri): String

    suspend fun deleteImage(name: String)

    suspend fun deleteFolder(folderName: String)
}