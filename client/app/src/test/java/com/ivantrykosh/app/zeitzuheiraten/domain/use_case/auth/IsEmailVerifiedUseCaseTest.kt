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
class IsEmailVerifiedUseCaseTest {

    private lateinit var userAuthRepositoryImpl: UserAuthRepositoryImpl
    private lateinit var isEmailVerifiedUseCase: IsEmailVerifiedUseCase

    @Before
    fun setup() {
        userAuthRepositoryImpl = mock()
        isEmailVerifiedUseCase = IsEmailVerifiedUseCase(userAuthRepositoryImpl)
    }

    @Test
    fun `is email verified executes successfully`() = runBlocking {
        val verified = true
        var isVerified = false
        whenever(userAuthRepositoryImpl.isEmailVerified()).doReturn(verified)

        isEmailVerifiedUseCase().collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { Assert.fail(result.error.message) }
                is Resource.Success -> { isVerified = result.data!! }
            }
        }

        verify(userAuthRepositoryImpl).isEmailVerified()
        Assert.assertEquals(verified, isVerified)
    }

    @Test(expected = CancellationException::class)
    fun `is email verified first emit must be loading`() = runBlocking {
        isEmailVerifiedUseCase().collect { result ->
            when (result) {
                is Resource.Loading -> { this.cancel() }
                is Resource.Error -> { Assert.fail("Loading must be first") }
                is Resource.Success -> { Assert.fail("Loading must be first") }
            }
        }
    }
}