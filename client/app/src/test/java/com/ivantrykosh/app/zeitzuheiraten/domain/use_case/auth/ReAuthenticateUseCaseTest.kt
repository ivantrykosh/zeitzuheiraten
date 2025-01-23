package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.auth

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
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
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class ReAuthenticateUseCaseTest {

    private lateinit var userAuthRepositoryImpl: UserAuthRepositoryImpl
    private lateinit var reAuthenticateUseCase: ReAuthenticateUseCase

    @Before
    fun setup() {
        userAuthRepositoryImpl = mock()
        reAuthenticateUseCase = ReAuthenticateUseCase(userAuthRepositoryImpl)
    }

    @Test
    fun `re authenticate successfully`() = runBlocking {
        val email = "test@email.com"
        val password = "Password123"
        var resourceSuccess = false
        whenever(userAuthRepositoryImpl.reAuthenticate(email, password)).doReturn(Unit)

        reAuthenticateUseCase(email, password).collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { Assert.fail(result.error.message) }
                is Resource.Success -> { resourceSuccess = true }
            }
        }

        verify(userAuthRepositoryImpl).reAuthenticate(email, password)
        Assert.assertTrue(resourceSuccess)
    }

    @Test
    fun `re authenticate failed because password is wrong`() = runBlocking {
        val email = "test@email.com"
        val password = "123"
        var exception: Exception? = null
        val mockException = mock<FirebaseAuthInvalidCredentialsException>()
        whenever(userAuthRepositoryImpl.reAuthenticate(email, password)).doAnswer { throw mockException }

        reAuthenticateUseCase(email, password).collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { exception = result.error }
                is Resource.Success -> { Assert.fail("Must be error that password is wrong") }
            }
        }

        verify(userAuthRepositoryImpl).reAuthenticate(email, password)
        Assert.assertNotNull(exception)
        Assert.assertTrue(exception is FirebaseAuthInvalidCredentialsException)
    }

    @Test(expected = CancellationException::class)
    fun `re authenticate first emit must be loading`() = runBlocking {
        val email = "test@email.com"
        val password = "Password123"
        reAuthenticateUseCase(email, password).collect { result ->
            when (result) {
                is Resource.Loading -> { this.cancel() }
                is Resource.Error -> { Assert.fail("Loading must be first") }
                is Resource.Success -> { Assert.fail("Loading must be first") }
            }
        }
    }
}