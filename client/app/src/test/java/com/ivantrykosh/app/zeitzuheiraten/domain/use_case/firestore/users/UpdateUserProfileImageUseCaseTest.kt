package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.users

import android.net.Uri
import com.ivantrykosh.app.zeitzuheiraten.data.repository.FirebaseStorageRepositoryImpl
import com.ivantrykosh.app.zeitzuheiraten.data.repository.UserRepositoryImpl
import com.ivantrykosh.app.zeitzuheiraten.domain.model.User
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class UpdateUserProfileImageUseCaseTest {

    private lateinit var userRepositoryImpl: UserRepositoryImpl
    private lateinit var firebaseStorageRepositoryImpl: FirebaseStorageRepositoryImpl
    private lateinit var updateUserProfileImageUseCase: UpdateUserProfileImageUseCase

    @Before
    fun setup() {
        userRepositoryImpl = mock()
        firebaseStorageRepositoryImpl = mock()
        updateUserProfileImageUseCase = UpdateUserProfileImageUseCase(userRepositoryImpl, firebaseStorageRepositoryImpl)
    }

    @Test
    fun `update user profile image successfully`() = runBlocking {
        val imageUri = mock<Uri>()
        val imageName = "imageName"
        val newUrl = "newImageUrl"
        val testUser = User(id = "t1e2s3t4", name = "Test User", email = "test@email.com", imageUrl = "imageUrl")
        var resourceSuccess = false
        whenever(firebaseStorageRepositoryImpl.uploadImage("${testUser.id}/$imageName", imageUri)).doReturn(newUrl)
        whenever(userRepositoryImpl.updateUser(testUser.copy(imageUrl = newUrl))).doReturn(Unit)

        updateUserProfileImageUseCase(testUser, imageUri, imageName).collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { Assert.fail(result.error.message) }
                is Resource.Success -> { resourceSuccess = true }
            }
        }

        verify(firebaseStorageRepositoryImpl).uploadImage("${testUser.id}/$imageName", imageUri)
        verify(userRepositoryImpl).updateUser(testUser.copy(imageUrl = newUrl))
        Assert.assertTrue(resourceSuccess)
    }

    @Test(expected = CancellationException::class)
    fun `update user profile image first emit must be loading`() = runBlocking {
        val imageUri = mock<Uri>()
        val imageName = "imageName"
        val testUser = User(id = "t1e2s3t4", name = "Test User", email = "test@email.com", imageUrl = "imageUrl")

        updateUserProfileImageUseCase(testUser, imageUri, imageName).collect { result ->
            when (result) {
                is Resource.Loading -> { this.cancel() }
                is Resource.Error -> { Assert.fail("Loading must be first") }
                is Resource.Success -> { Assert.fail("Loading must be first") }
            }
        }
    }
}