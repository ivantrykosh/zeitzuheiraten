package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.auth

import com.ivantrykosh.app.zeitzuheiraten.domain.model.User
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserAuthRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserRepository
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
class SignInUseCaseTest {

    private lateinit var userAuthRepository: UserAuthRepository
    private lateinit var userRepository: UserRepository
    private lateinit var signInUseCase: SignInUseCase

    @Before
    fun setup() {
        userAuthRepository = mock()
        userRepository = mock()
        signInUseCase = SignInUseCase(userAuthRepository, userRepository)
    }

    @Test
    fun `sign in successfully`() = runBlocking {
        val email = "test@email.com"
        val password = "Password123"
        val userId = "t1e2s3t4"
        val provider = true
        val user = User(
            id = userId,
            isProvider = provider,
        )
        var isProvider = false
        whenever(userAuthRepository.signIn(email, password)).doReturn(Unit)
        whenever(userAuthRepository.getCurrentUserId()).doReturn(userId)
        whenever(userRepository.getUserById(userId)).doReturn(user)

        signInUseCase(email, password).collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { Assert.fail(result.error.message) }
                is Resource.Success -> { isProvider = result.data!! }
            }
        }

        verify(userAuthRepository).signIn(email, password)
        verify(userAuthRepository).getCurrentUserId()
        verify(userRepository).getUserById(userId)
        Assert.assertEquals(provider, isProvider)
    }

    @Test
    fun `sign in failed because email is wrong`() = runBlocking {
        val email = "wrong@email"
        val password = "Password123"
        var exception: Exception? = null
        val mockException = RuntimeException("error")
        whenever(userAuthRepository.signIn(email, password)).doAnswer { throw mockException }

        signInUseCase(email, password).collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { exception = result.error }
                is Resource.Success -> { Assert.fail("Must be error that email is wrong") }
            }
        }

        verify(userAuthRepository).signIn(email, password)
        Assert.assertNotNull(exception)
        Assert.assertTrue(exception is RuntimeException)
    }

    @Test
    fun `sign in failed because password is wrong`() = runBlocking {
        val email = "test@email.com"
        val password = "123"
        var exception: Exception? = null
        val mockException = RuntimeException("error")
        whenever(userAuthRepository.signIn(email, password)).doAnswer { throw mockException }

        signInUseCase(email, password).collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { exception = result.error }
                is Resource.Success -> { Assert.fail("Must be error that password is wrong") }
            }
        }

        verify(userAuthRepository).signIn(email, password)
        Assert.assertNotNull(exception)
        Assert.assertTrue(exception is RuntimeException)
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