package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.users

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
class UpdateUserUseCaseTest {

    private lateinit var userRepositoryImpl: UserRepositoryImpl
    private lateinit var updateUserUseCase: UpdateUserUseCase

    @Before
    fun setup() {
        userRepositoryImpl = mock()
        updateUserUseCase = UpdateUserUseCase(userRepositoryImpl)
    }

    @Test
    fun `update user successfully`() = runBlocking {
        val testUser = User(id = "t1e2s3t4", name = "Test User", email = "test@email.com")
        var resourceSuccess = false
        whenever(userRepositoryImpl.updateUser(testUser)).doReturn(Unit)

        updateUserUseCase(testUser).collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { Assert.fail(result.error.message) }
                is Resource.Success -> { resourceSuccess = true }
            }
        }

        verify(userRepositoryImpl).updateUser(testUser)
        Assert.assertTrue(resourceSuccess)
    }

    @Test(expected = CancellationException::class)
    fun `update user first emit must be loading`() = runBlocking {
        val testUser = User(id = "t1e2s3t4", name = "Test User", email = "test@email.com")
        whenever(userRepositoryImpl.updateUser(testUser)).doReturn(Unit)

        updateUserUseCase(testUser).collect { result ->
            when (result) {
                is Resource.Loading -> { this.cancel() }
                is Resource.Error -> { Assert.fail("Loading must be first") }
                is Resource.Success -> { Assert.fail("Loading must be first") }
            }
        }
    }
}