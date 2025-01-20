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
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class DoesUserLogInAndIsEmailVerifiedUseCaseTest {

    private lateinit var userAuthRepositoryImpl: UserAuthRepositoryImpl
    private lateinit var doesUserLogInAndIsEmailVerifiedUseCase: DoesUserLogInAndIsEmailVerifiedUseCase

    @Before
    fun setup() {
        userAuthRepositoryImpl = mock()
        doesUserLogInAndIsEmailVerifiedUseCase = DoesUserLogInAndIsEmailVerifiedUseCase(userAuthRepositoryImpl)
    }

    @Test
    fun `user logged in and email is verified`() = runBlocking {
        val id = "t1e2s3t4"
        val verified = true
        var success = false
        whenever(userAuthRepositoryImpl.getCurrentUserId()).doReturn(id)
        whenever(userAuthRepositoryImpl.isEmailVerified()).doReturn(verified)

        doesUserLogInAndIsEmailVerifiedUseCase().collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { Assert.fail(result.error.message) }
                is Resource.Success -> { success = result.data!! }
            }
        }

        verify(userAuthRepositoryImpl).getCurrentUserId()
        verify(userAuthRepositoryImpl).isEmailVerified()
        Assert.assertTrue(success)
    }

    @Test
    fun `user logged in and email is not verified`() = runBlocking {
        val id = "t1e2s3t4"
        val verified = false
        var success = true
        whenever(userAuthRepositoryImpl.getCurrentUserId()).doReturn(id)
        whenever(userAuthRepositoryImpl.isEmailVerified()).doReturn(verified)

        doesUserLogInAndIsEmailVerifiedUseCase().collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { Assert.fail(result.error.message) }
                is Resource.Success -> { success = result.data!! }
            }
        }

        verify(userAuthRepositoryImpl).getCurrentUserId()
        verify(userAuthRepositoryImpl).isEmailVerified()
        Assert.assertFalse(success)
    }

    @Test
    fun `user does not log in`() = runBlocking {
        val id = ""
        var success = true
        whenever(userAuthRepositoryImpl.getCurrentUserId()).doReturn(id)

        doesUserLogInAndIsEmailVerifiedUseCase().collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { Assert.fail(result.error.message) }
                is Resource.Success -> { success = result.data!! }
            }
        }

        verify(userAuthRepositoryImpl).getCurrentUserId()
        Assert.assertFalse(success)
    }

    @Test(expected = CancellationException::class)
    fun `first emit must be loading`() = runBlocking {
        doesUserLogInAndIsEmailVerifiedUseCase().collect { result ->
            when (result) {
                is Resource.Loading -> { this.cancel() }
                is Resource.Error -> { Assert.fail("Loading must be first") }
                is Resource.Success -> { Assert.fail("Loading must be first") }
            }
        }
    }
}