package com.ivantrykosh.app.zeitzuheiraten.domain.repository

import com.ivantrykosh.app.zeitzuheiraten.domain.model.User

interface UserRepository {

    suspend fun createUser(user: User)

    suspend fun getUserById(userId: String): User

    suspend fun updateUser(user: User)

    suspend fun deleteUser(userId: String)
}