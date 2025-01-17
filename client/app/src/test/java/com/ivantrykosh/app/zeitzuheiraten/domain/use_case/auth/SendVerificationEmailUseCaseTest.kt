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
class SendVerificationEmailUseCaseTest {

    private lateinit var userAuthRepositoryImpl: UserAuthRepositoryImpl
    private lateinit var sendVerificationEmailUseCase: SendVerificationEmailUseCase

    @Before
    fun setup() {
        userAuthRepositoryImpl = mock()
        sendVerificationEmailUseCase = SendVerificationEmailUseCase(userAuthRepositoryImpl)
    }

    @Test
    fun `send verification email successfully`() = runBlocking {
        var resourceSuccess = false
        whenever(userAuthRepositoryImpl.sendVerificationEmail()).doReturn(Unit)

        sendVerificationEmailUseCase().collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { Assert.fail(result.message) }
                is Resource.Success -> { resourceSuccess = true }
            }
        }

        verify(userAuthRepositoryImpl).sendVerificationEmail()
        Assert.assertTrue(resourceSuccess)
    }

    @Test(expected = CancellationException::class)
    fun `send verification email first emit must be loading`() = runBlocking {
        sendVerificationEmailUseCase().collect { result ->
            when (result) {
                is Resource.Loading -> { this.cancel() }
                is Resource.Error -> { Assert.fail("Loading must be first") }
                is Resource.Success -> { Assert.fail("Loading must be first") }
            }
        }
    }
}