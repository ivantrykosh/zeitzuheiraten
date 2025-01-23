package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.users

import android.net.Uri
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.StorageException
import com.ivantrykosh.app.zeitzuheiraten.data.repository.FirebaseStorageRepositoryImpl
import com.ivantrykosh.app.zeitzuheiraten.data.repository.UserAuthRepositoryImpl
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
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class CreateUserUseCaseTest {

    private lateinit var userAuthRepositoryImpl: UserAuthRepositoryImpl
    private lateinit var userRepositoryImpl: UserRepositoryImpl
    private lateinit var firebaseStorageRepositoryImpl: FirebaseStorageRepositoryImpl
    private lateinit var createUserUseCase: CreateUserUseCase

    @Before
    fun setup() {
        userAuthRepositoryImpl = mock()
        userRepositoryImpl = mock()
        firebaseStorageRepositoryImpl = mock()
        createUserUseCase = CreateUserUseCase(userAuthRepositoryImpl, userRepositoryImpl, firebaseStorageRepositoryImpl)
    }

    @Test
    fun `create user successfully`() = runBlocking {
        val email = "test@email.com"
        val password = "Password123"
        val userId = "t1e2s3t4"
        val imageUri = mock<Uri>()
        val imageName = "avatar.jpg"
        val downloadUrl = "some-bucket/$userId/$imageName"
        val user = User(name = "Test User", email = "test@email.com")
        var resourceSuccess = false
        whenever(userAuthRepositoryImpl.signUp(email, password)).doReturn(Unit)
        whenever(userAuthRepositoryImpl.getCurrentUserId()).doReturn(userId)
        whenever(firebaseStorageRepositoryImpl.uploadImage("$userId/$imageName", imageUri)).doReturn(downloadUrl)
        whenever(userRepositoryImpl.createUser(user.copy(id = userId, imageUrl = downloadUrl))).doReturn(Unit)

        createUserUseCase(email, password, user, imageUri, imageName).collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { Assert.fail(result.error.message) }
                is Resource.Success -> { resourceSuccess = true }
            }
        }

        verify(userAuthRepositoryImpl).signUp(email, password)
        verify(userAuthRepositoryImpl).getCurrentUserId()
        verify(firebaseStorageRepositoryImpl).uploadImage("$userId/$imageName", imageUri)
        verify(userRepositoryImpl).createUser(user.copy(id = userId, imageUrl = downloadUrl))
        Assert.assertTrue(resourceSuccess)
    }

    @Test
    fun `create user failed because credentials is incorrect`() = runBlocking {
        val email = "test@email"
        val password = "pass"
        val imageUri = mock<Uri>()
        val imageName = "avatar.jpg"
        val user = User(id = "t1e2s3t4", name = "Test User", email = "test@email")
        var exception: Exception? = null
        val mockException = mock<FirebaseAuthInvalidCredentialsException>()
        whenever(userAuthRepositoryImpl.signUp(email, password)).doAnswer { throw mockException }
        whenever(userAuthRepositoryImpl.getCurrentUserId()).doReturn("")

        createUserUseCase(email, password, user, imageUri, imageName).collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { exception = result.error }
                is Resource.Success -> { Assert.fail("Must be error") }
            }
        }

        verify(userAuthRepositoryImpl).signUp(email, password)
        Assert.assertNotNull(exception)
        Assert.assertTrue(exception is FirebaseAuthInvalidCredentialsException)
    }

    @Test
    fun `create user failed because fail image uploading to storage`() = runBlocking {
        val email = "test@email.com"
        val password = "Password123"
        val userId = "t1e2s3t4"
        val imageUri = mock<Uri>()
        val imageName = "avatar.jpg"
        val user = User(name = "Test User", email = "test@email.com")
        var exception: Exception? = null
        val mockException = mock<StorageException>()
        whenever(userAuthRepositoryImpl.signUp(email, password)).doReturn(Unit)
        whenever(userAuthRepositoryImpl.getCurrentUserId()).doReturn(userId)
        whenever(firebaseStorageRepositoryImpl.uploadImage("$userId/$imageName", imageUri)).doAnswer { throw mockException }
        whenever(userAuthRepositoryImpl.deleteCurrentUser()).doReturn(Unit)

        createUserUseCase(email, password, user, imageUri, imageName).collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { exception = result.error }
                is Resource.Success -> { Assert.fail("Must be error") }
            }
        }

        verify(userAuthRepositoryImpl).signUp(email, password)
        verify(firebaseStorageRepositoryImpl).uploadImage("$userId/$imageName", imageUri)
        verify(userAuthRepositoryImpl).deleteCurrentUser()
        Assert.assertNotNull(exception)
        Assert.assertTrue(exception is StorageException)
    }

    @Test
    fun `create user failed because fail user creation in storage`() = runBlocking {
        val email = "test@email.com"
        val password = "Password123"
        val userId = "t1e2s3t4"
        val imageUri = mock<Uri>()
        val imageName = "avatar.jpg"
        val downloadUrl = "some-bucket/$userId/$imageName"
        val user = User(name = "Test User", email = "test@email.com")
        var exception: Exception? = null
        val mockException = mock<FirebaseFirestoreException>()
        whenever(userAuthRepositoryImpl.signUp(email, password)).doReturn(Unit)
        whenever(userAuthRepositoryImpl.getCurrentUserId()).doReturn(userId)
        whenever(firebaseStorageRepositoryImpl.uploadImage("$userId/$imageName", imageUri)).doReturn(downloadUrl)
        whenever(userAuthRepositoryImpl.deleteCurrentUser()).doReturn(Unit)
        whenever(firebaseStorageRepositoryImpl.deleteImage("$userId/$imageName")).doReturn(Unit)
        whenever(userRepositoryImpl.createUser(user.copy(id = userId, imageUrl = downloadUrl))).doAnswer { throw mockException }

        createUserUseCase(email, password, user, imageUri, imageName).collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { exception = result.error }
                is Resource.Success -> { Assert.fail("Must be error") }
            }
        }

        verify(userAuthRepositoryImpl).signUp(email, password)
        verify(firebaseStorageRepositoryImpl).uploadImage("$userId/$imageName", imageUri)
        verify(userRepositoryImpl).createUser(user.copy(id = userId, imageUrl = downloadUrl))
        verify(userAuthRepositoryImpl).deleteCurrentUser()
        verify(firebaseStorageRepositoryImpl).deleteImage("$userId/$imageName")
        Assert.assertNotNull(exception)
        Assert.assertTrue(exception is FirebaseFirestoreException)
    }

    @Test(expected = CancellationException::class)
    fun `create user first emit must be loading`() = runBlocking {
        val email = "test@email"
        val password = "pass"
        val imageUri = mock<Uri>()
        val imageName = "avatar.jpg"
        val user = User(id = "t1e2s3t4", name = "Test User", email = "test@email.com")
        whenever(userAuthRepositoryImpl.getCurrentUserId()).doReturn("")

        createUserUseCase(email, password, user, imageUri, imageName).collect { result ->
            when (result) {
                is Resource.Loading -> { this.cancel() }
                is Resource.Error -> { Assert.fail("Loading must be first") }
                is Resource.Success -> { Assert.fail("Loading must be first") }
            }
        }
    }
}