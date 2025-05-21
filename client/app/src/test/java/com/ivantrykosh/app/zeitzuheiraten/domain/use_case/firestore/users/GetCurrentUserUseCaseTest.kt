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
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class GetCurrentUserUseCaseTest {

    private lateinit var userRepositoryImpl: UserRepositoryImpl
    private lateinit var userAuthRepositoryImpl: UserAuthRepositoryImpl
    private lateinit var getCurrentUserUseCase: GetCurrentUserUseCase

    @Before
    fun setup() {
        userAuthRepositoryImpl = mock()
        userRepositoryImpl = mock()
        getCurrentUserUseCase = GetCurrentUserUseCase(userAuthRepositoryImpl, userRepositoryImpl)
    }

    @Test
    fun `get current user successfully`() = runBlocking {
        val userId = "t1e2s3t4"
        var user = User()
        val testUser = User(id = userId, name = "Test User", email = "test@email.com")
        whenever(userAuthRepositoryImpl.getCurrentUserId()).doReturn(userId)
        whenever(userRepositoryImpl.getUserById(userId)).doReturn(testUser)

        getCurrentUserUseCase().collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { Assert.fail(result.error.message) }
                is Resource.Success -> { user = result.data!! }
            }
        }

        verify(userAuthRepositoryImpl).getCurrentUserId()
        verify(userRepositoryImpl).getUserById(userId)
        Assert.assertEquals(testUser.id, user.id)
        Assert.assertEquals(testUser.name, user.name)
        Assert.assertEquals(testUser.email, user.email)
    }

    @Test(expected = CancellationException::class)
    fun `get current user first emit must be loading`() = runBlocking {
        getCurrentUserUseCase().collect { result ->
            when (result) {
                is Resource.Loading -> { this.cancel() }
                is Resource.Error -> { Assert.fail("Loading must be first") }
                is Resource.Success -> { Assert.fail("Loading must be first") }
            }
        }
    }
}