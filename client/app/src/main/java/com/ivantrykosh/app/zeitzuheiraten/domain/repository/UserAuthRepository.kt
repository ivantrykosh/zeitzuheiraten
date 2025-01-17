package com.ivantrykosh.app.zeitzuheiraten.domain.repository

interface UserAuthRepository {

    suspend fun signUp(email: String, password: String)

    suspend fun signIn(email: String, password: String)

    suspend fun signOut()

    suspend fun resetPassword(email: String)

    suspend fun deleteCurrentUser()

    suspend fun getCurrentUserId(): String

    suspend fun sendVerificationEmail()

    suspend fun isEmailVerified(): Boolean
}