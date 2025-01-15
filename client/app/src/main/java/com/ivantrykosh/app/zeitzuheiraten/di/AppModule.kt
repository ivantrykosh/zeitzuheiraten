package com.ivantrykosh.app.zeitzuheiraten.di

import com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.auth.FirebaseAuth
import com.ivantrykosh.app.zeitzuheiraten.data.repository.UserAuthRepositoryImpl
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserAuthRepository
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
    abstract fun bindUserAuthRepository(userAuthRepositoryImpl: UserAuthRepositoryImpl): UserAuthRepository
}