package com.ivantrykosh.app.zeitzuheiraten.di

import com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.auth.FirebaseAuth
import com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.firestore.FirestoreFeedbacks
import com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.firestore.FirestorePosts
import com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.firestore.FirestoreUsers
import com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.storage.FirebaseStorage
import com.ivantrykosh.app.zeitzuheiraten.data.repository.FeedbackRepositoryImpl
import com.ivantrykosh.app.zeitzuheiraten.data.repository.FirebaseStorageRepositoryImpl
import com.ivantrykosh.app.zeitzuheiraten.data.repository.PostRepositoryImpl
import com.ivantrykosh.app.zeitzuheiraten.data.repository.UserAuthRepositoryImpl
import com.ivantrykosh.app.zeitzuheiraten.data.repository.UserRepositoryImpl
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.FeedbackRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.FirebaseStorageRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.PostRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserAuthRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModuleBindings {

    @Singleton
    @Binds
    abstract fun bindUserAuthRepository(userAuthRepositoryImpl: UserAuthRepositoryImpl): UserAuthRepository

    @Singleton
    @Binds
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    @Singleton
    @Binds
    abstract fun bindPostRepository(postRepositoryImpl: PostRepositoryImpl): PostRepository

    @Singleton
    @Binds
    abstract fun bindFeedbackRepository(feedbackRepositoryImpl: FeedbackRepositoryImpl): FeedbackRepository

    @Singleton
    @Binds
    abstract fun bindFirebaseStorageRepository(firebaseStorageRepositoryImpl: FirebaseStorageRepositoryImpl): FirebaseStorageRepository
}

@Module
@InstallIn(SingletonComponent::class)
object AppModuleProvidings {

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth()
    }

    @Singleton
    @Provides
    fun provideFirestoreUsers(): FirestoreUsers {
        return FirestoreUsers()
    }

    @Singleton
    @Provides
    fun provideFirestorePosts(): FirestorePosts {
        return FirestorePosts()
    }

    @Singleton
    @Provides
    fun provideFirestoreFeedbacks(): FirestoreFeedbacks {
        return FirestoreFeedbacks()
    }

    @Singleton
    @Provides
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage()
    }
}