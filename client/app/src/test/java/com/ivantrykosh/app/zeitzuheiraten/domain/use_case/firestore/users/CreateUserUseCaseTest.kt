package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.users

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
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class CreateUserUseCaseTest {

    private lateinit var userAuthRepositoryImpl: UserAuthRepositoryImpl
    private lateinit var userRepositoryImpl: UserRepositoryImpl
    private lateinit var createUserUseCase: CreateUserUseCase

    @Before
    fun setup() {
        userAuthRepositoryImpl = mock()
        userRepositoryImpl = mock()
        createUserUseCase = CreateUserUseCase(userAuthRepositoryImpl, userRepositoryImpl)
    }

    @Test
    fun `create user successfully`() = runBlocking {
        val email = "test@email.com"
        val password = "Password123"
        val userId = "t1e2s3t4"
        val user = User(name = "Test User", email = "test@email.com")
        var resourceSuccess = false
        whenever(userAuthRepositoryImpl.signUp(email, password)).doReturn(Unit)
        whenever(userAuthRepositoryImpl.getCurrentUserId()).doReturn(userId)
        whenever(userRepositoryImpl.createUser(user.copy(id = userId))).doReturn(Unit)

        createUserUseCase(email, password, user).collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { Assert.fail(result.message) }
                is Resource.Success -> { resourceSuccess = true }
            }
        }

        verify(userAuthRepositoryImpl).signUp(email, password)
        verify(userAuthRepositoryImpl).getCurrentUserId()
        verify(userRepositoryImpl).createUser(user.copy(id = userId))
        Assert.assertTrue(resourceSuccess)
    }

    @Test(expected = CancellationException::class)
    fun `create user failed because credentials is incorrect`() = runBlocking {
        val email = "test@email"
        val password = "pass"
        val user = User(id = "t1e2s3t4", name = "Test User", email = "test@email")
        whenever(userAuthRepositoryImpl.signUp(email, password)).doThrow(RuntimeException("Credentials is incorrect"))
        whenever(userAuthRepositoryImpl.getCurrentUserId()).doReturn("")

        createUserUseCase(email, password, user).collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { this.cancel() }
                is Resource.Success -> { Assert.fail("Must be error") }
            }
        }
    }

    @Test
    fun `create user failed because fail user creation in storage`() = runBlocking {
        val email = "test@email.com"
        val password = "Password123"
        val userId = "t1e2s3t4"
        val user = User(name = "Test User", email = "test@email.com")
        var resourceError = false
        whenever(userAuthRepositoryImpl.signUp(email, password)).doReturn(Unit)
        whenever(userAuthRepositoryImpl.getCurrentUserId()).doReturn(userId)
        whenever(userAuthRepositoryImpl.deleteCurrentUser()).doReturn(Unit)
        whenever(userRepositoryImpl.createUser(user.copy(id = userId))).doThrow(RuntimeException("Fail to create user in storage"))

        createUserUseCase(email, password, user).collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { resourceError = true }
                is Resource.Success -> { Assert.fail("Must be error") }
            }
        }

        verify(userAuthRepositoryImpl).signUp(email, password)
        verify(userRepositoryImpl).createUser(user.copy(id = userId))
        verify(userAuthRepositoryImpl).deleteCurrentUser()
        Assert.assertTrue(resourceError)
    }

    @Test(expected = CancellationException::class)
    fun `create user first emit must be loading`() = runBlocking {
        val email = "test@email"
        val password = "pass"
        val user = User(id = "t1e2s3t4", name = "Test User", email = "test@email.com")
        whenever(userAuthRepositoryImpl.getCurrentUserId()).doReturn("")

        createUserUseCase(email, password, user).collect { result ->
            when (result) {
                is Resource.Loading -> { this.cancel() }
                is Resource.Error -> { Assert.fail("Loading must be first") }
                is Resource.Success -> { Assert.fail("Loading must be first") }
            }
        }
    }
}