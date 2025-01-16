package com.ivantrykosh.app.zeitzuheiraten.data.repository

import com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.firestore.FirestoreUsers
import com.ivantrykosh.app.zeitzuheiraten.domain.model.User
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firestoreUsers: FirestoreUsers
) : UserRepository {
    override suspend fun createUser(user: User) {
        firestoreUsers.createUser(user)
    }

    override suspend fun getUserById(userId: String): User {
        return firestoreUsers.getUserById(userId)
    }

    override suspend fun updateUser(user: User) {
        firestoreUsers.updateUser(user)
    }

    override suspend fun deleteUser(userId: String) {
        firestoreUsers.deleteUser(userId)
    }
}