package com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.storage

import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await

class FirebaseStorage(private val firebaseStorage: FirebaseStorage = Firebase.storage) {

    /**
     * Upload image and return download URL
     */
    suspend fun uploadImage(name: String, imageUri: Uri): String {
        val snapshot = firebaseStorage.reference
            .child(name)
            .putFile(imageUri)
            .await()
        val downloadUri = snapshot.metadata!!.reference!!.downloadUrl.await()
        return downloadUri.toString()
    }

    suspend fun deleteImageOrFolder(name: String) {
        firebaseStorage.reference
            .child(name)
            .delete()
            .await()
    }
}