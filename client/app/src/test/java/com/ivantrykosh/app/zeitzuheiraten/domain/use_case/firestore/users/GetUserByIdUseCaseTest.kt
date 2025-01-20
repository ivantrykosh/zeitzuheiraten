package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.users

import com.google.firebase.firestore.FirebaseFirestoreException
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
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class GetUserByIdUseCaseTest {

    private lateinit var userRepositoryImpl: UserRepositoryImpl
    private lateinit var getUserByIdUseCase: GetUserByIdUseCase

    @Before
    fun setup() {
        userRepositoryImpl = mock()
        getUserByIdUseCase = GetUserByIdUseCase(userRepositoryImpl)
    }

    @Test
    fun `get user by id successfully`() = runBlocking {
        val userId = "t1e2s3t4"
        var user = User()
        val testUser = User(id = userId, name = "Test User", email = "test@email.com")
        whenever(userRepositoryImpl.getUserById(userId)).doReturn(testUser)

        getUserByIdUseCase(userId).collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { Assert.fail(result.error.message) }
                is Resource.Success -> { user = result.data!! }
            }
        }

        verify(userRepositoryImpl).getUserById(userId)
        Assert.assertEquals(testUser.id, user.id)
        Assert.assertEquals(testUser.name, user.name)
        Assert.assertEquals(testUser.email, user.email)
    }

    @Test
    fun `get user by id failed because user does not exist`() = runBlocking {
        val userId = "wrongId"
        var exception: Exception? = null
        val mockException = mock<FirebaseFirestoreException>()
        whenever(userRepositoryImpl.getUserById(userId)).doAnswer { throw mockException }

        getUserByIdUseCase(userId).collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { exception = result.error }
                is Resource.Success -> { Assert.fail("Must be error") }
            }
        }

        verify(userRepositoryImpl).getUserById(userId)
        Assert.assertNotNull(exception)
        Assert.assertTrue(exception is FirebaseFirestoreException)
    }

    @Test(expected = CancellationException::class)
    fun `get user by id first emit must be loading`() = runBlocking {
        val userId = "t1e2s3t4"
        val testUser = User(id = userId, name = "Test User", email = "test@email.com")
        whenever(userRepositoryImpl.getUserById(userId)).doReturn(testUser)

        getUserByIdUseCase(userId).collect { result ->
            when (result) {
                is Resource.Loading -> { this.cancel() }
                is Resource.Error -> { Assert.fail("Loading must be first") }
                is Resource.Success -> { Assert.fail("Loading must be first") }
            }
        }
    }
}