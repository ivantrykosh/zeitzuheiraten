package com.ivantrykosh.app.zeitzuheiraten.domain.repository

interface UserAuthRepository {

    suspend fun signUp(email: String, password: String)

    suspend fun signIn(email: String, password: String)

    suspend fun signOut()

    suspend fun deleteCurrentUser()

    suspend fun getCurrentUserId(): String

    suspend fun reAuthenticate(email: String, password: String)
}