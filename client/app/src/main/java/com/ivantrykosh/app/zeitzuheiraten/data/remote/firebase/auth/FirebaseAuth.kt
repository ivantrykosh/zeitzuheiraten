package com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await

class FirebaseAuth(private val firebaseAuth: FirebaseAuth = Firebase.auth) {

    suspend fun signUp(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).await()
    }

    suspend fun signIn(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password).await()
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    suspend fun deleteCurrentUser() {
        firebaseAuth.currentUser!!.delete().await()
    }

    /**
     * Returns user id if user exists, otherwise returns empty string
     */
    fun getCurrentUserId(): String {
        return firebaseAuth.currentUser?.uid ?: ""
    }

    suspend fun reAuthenticate(email: String, password: String) {
        val credential = EmailAuthProvider.getCredential(email, password)
        firebaseAuth.currentUser!!.reauthenticate(credential).await()
    }
}