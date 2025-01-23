package com.ivantrykosh.app.zeitzuheiraten.data.repository

import com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.auth.FirebaseAuth
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserAuthRepository
import javax.inject.Inject

class UserAuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : UserAuthRepository {
    override suspend fun signUp(email: String, password: String) {
        firebaseAuth.signUp(email, password)
    }

    override suspend fun signIn(email: String, password: String) {
        firebaseAuth.signIn(email, password)
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }

    override suspend fun resetPassword(email: String) {
        firebaseAuth.resetPassword(email)
    }

    override suspend fun deleteCurrentUser() {
        firebaseAuth.deleteCurrentUser()
    }

    override suspend fun getCurrentUserId(): String {
        return firebaseAuth.getCurrentUserId()
    }

    override suspend fun sendVerificationEmail() {
        firebaseAuth.sendVerificationEmail()
    }

    override suspend fun isEmailVerified(): Boolean {
        return firebaseAuth.isEmailVerified()
    }

    override suspend fun reAuthenticate(email: String, password: String) {
        firebaseAuth.reAuthenticate(email, password)
    }
}