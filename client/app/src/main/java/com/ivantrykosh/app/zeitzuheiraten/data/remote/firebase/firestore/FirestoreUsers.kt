package com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.firestore

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.ivantrykosh.app.zeitzuheiraten.domain.model.User
import com.ivantrykosh.app.zeitzuheiraten.utils.Collections
import kotlinx.coroutines.tasks.await

class FirestoreUsers(private val firestore: FirebaseFirestore = Firebase.firestore) {

    suspend fun createUser(user: User) {
        val userData = mapOf(
            User::name.name to user.name,
            User::email.name to user.email,
            User::imageUrl.name to user.imageUrl,
            User::isProvider.name to user.isProvider,
        )
        firestore.collection(Collections.USERS)
            .document(user.id)
            .set(userData)
            .await()
    }

    suspend fun getUserById(userId: String): User {
        return firestore.collection(Collections.USERS)
            .document(userId)
            .get()
            .await()
            .toObject(User::class.java)!!
            .copy(id = userId)
    }

    /**
     * Update user data. Only name and imageUrl can be updated
     */
    suspend fun updateUser(user: User) {
        val userData = mapOf(
            User::name.name to user.name,
            User::imageUrl.name to user.imageUrl,
        )
        firestore.collection(Collections.USERS)
            .document(user.id)
            .update(userData)
            .await()
    }

    suspend fun deleteUser(userId: String) {
        firestore.collection(Collections.USERS)
            .document(userId)
            .delete()
            .await()
    }
}