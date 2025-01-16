package com.ivantrykosh.app.zeitzuheiraten.di

import com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.auth.FirebaseAuth
import com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.firestore.FirestoreUsers
import com.ivantrykosh.app.zeitzuheiraten.data.repository.UserAuthRepositoryImpl
import com.ivantrykosh.app.zeitzuheiraten.data.repository.UserRepositoryImpl
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserAuthRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    abstract fun bindFirebaseAuth(firebaseAuth: FirebaseAuth): FirebaseAuth

    @Binds
    abstract fun bindFirestoreUsers(firestoreUsers: FirestoreUsers): FirestoreUsers

    @Binds
    abstract fun bindUserAuthRepository(userAuthRepositoryImpl: UserAuthRepositoryImpl): UserAuthRepository

    @Binds
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository
}