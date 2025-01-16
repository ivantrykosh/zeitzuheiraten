package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.auth

import com.ivantrykosh.app.zeitzuheiraten.data.repository.UserAuthRepositoryImpl
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
class SignInUseCaseTest {

    private lateinit var userAuthRepositoryImpl: UserAuthRepositoryImpl
    private lateinit var signInUseCase: SignInUseCase

    @Before
    fun setup() {
        userAuthRepositoryImpl = mock()
        signInUseCase = SignInUseCase(userAuthRepositoryImpl)
    }

    @Test
    fun `sign in successfully`() = runBlocking {
        val email = "test@email.com"
        val password = "Password123"
        val userId = "t1e2s3t4"
        var id = ""
        whenever(userAuthRepositoryImpl.signIn(email, password)).doReturn(Unit)
        whenever(userAuthRepositoryImpl.getCurrentUserId()).doReturn(userId)

        signInUseCase(email, password).collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { Assert.fail(result.message) }
                is Resource.Success -> { id = result.data!! }
            }
        }

        verify(userAuthRepositoryImpl).signIn(email, password)
        verify(userAuthRepositoryImpl).getCurrentUserId()
        Assert.assertEquals(userId, id)
    }

    @Test
    fun `sign in failed because email is wrong`() = runBlocking {
        val email = "wrong@email"
        val password = "Password123"
        val wrongEmailMessage = "Email is wrong"
        var message = ""
        whenever(userAuthRepositoryImpl.signIn(email, password)).doThrow(RuntimeException(wrongEmailMessage))

        signInUseCase(email, password).collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { message = result.message }
                is Resource.Success -> { Assert.fail("Must be error that email is wrong") }
            }
        }

        verify(userAuthRepositoryImpl).signIn(email, password)
        Assert.assertEquals(wrongEmailMessage, message)
    }

    @Test
    fun `sign in failed because password is wrong`() = runBlocking {
        val email = "test@email.com"
        val password = "123"
        val wrongPasswordMessage = "Password is wrong"
        var message = ""
        whenever(userAuthRepositoryImpl.signIn(email, password)).thenThrow(RuntimeException(wrongPasswordMessage))

        signInUseCase(email, password).collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { message = result.message }
                is Resource.Success -> { Assert.fail("Must be error that password is wrong") }
            }
        }

        verify(userAuthRepositoryImpl).signIn(email, password)
        Assert.assertEquals(wrongPasswordMessage, message)
    }

    @Test(expected = CancellationException::class)
    fun `sign in first emit must be loading`() = runBlocking {
        val email = "test@email.com"
        val password = "Password123"
        signInUseCase(email, password).collect { result ->
            when (result) {
                is Resource.Loading -> { this.cancel() }
                is Resource.Error -> { Assert.fail("Loading must be first") }
                is Resource.Success -> { Assert.fail("Loading must be first") }
            }
        }
    }
}