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

    suspend fun deleteImage(name: String) {
        firebaseStorage.reference
            .child(name)
            .delete()
            .await()
    }

    suspend fun deleteFolder(folderName: String) {
        val folderRef = firebaseStorage.reference.child(folderName)
        val result = folderRef.listAll().await()
        for (fileRef in result.items) {
            fileRef.delete().await()
        }
        for (prefixRef in result.prefixes) {
            deleteFolder(prefixRef.path)
        }
    }
}