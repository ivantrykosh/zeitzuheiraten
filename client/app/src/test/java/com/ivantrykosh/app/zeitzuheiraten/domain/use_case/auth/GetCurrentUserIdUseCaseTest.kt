package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.auth

import com.ivantrykosh.app.zeitzuheiraten.data.repository.UserAuthRepositoryImpl
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class GetCurrentUserIdUseCaseTest {

    private lateinit var userAuthRepositoryImpl: UserAuthRepositoryImpl
    private lateinit var getCurrentUserIdUseCase: GetCurrentUserIdUseCase

    @Before
    fun setup() {
        userAuthRepositoryImpl = mock()
        getCurrentUserIdUseCase = GetCurrentUserIdUseCase(userAuthRepositoryImpl)
    }

    @Test
    fun `get current user id successfully`() = runBlocking {
        val userId = "t1e2s3t4"
        var testUserId = ""
        whenever(userAuthRepositoryImpl.getCurrentUserId()).doReturn(userId)

        getCurrentUserIdUseCase().collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { Assert.fail(result.message) }
                is Resource.Success -> { testUserId = result.data!! }
            }
        }

        verify(userAuthRepositoryImpl).getCurrentUserId()
        assertEquals(userId, testUserId)
    }

    @Test(expected = CancellationException::class)
    fun `get current user id first emit must be loading`() = runBlocking {
        getCurrentUserIdUseCase().collect { result ->
            when (result) {
                is Resource.Loading -> { this.cancel() }
                is Resource.Error -> { Assert.fail("Loading must be first") }
                is Resource.Success -> { Assert.fail("Loading must be first") }
            }
        }
    }
}