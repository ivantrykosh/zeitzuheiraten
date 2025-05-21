package com.ivantrykosh.app.zeitzuheiraten.data.repository

import com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.auth.FirebaseAuth
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.stubbing.Answer

@RunWith(MockitoJUnitRunner::class)
class UserAuthRepositoryImplTest {

    private lateinit var mockFirebaseAuth: FirebaseAuth
    private lateinit var userAuthRepositoryImpl: UserAuthRepositoryImpl

    @Before
    fun setup() {
        mockFirebaseAuth = mock()
        userAuthRepositoryImpl = UserAuthRepositoryImpl(mockFirebaseAuth)
    }

    @Test
    fun `sign up successfully`() = runTest {
        val email = "test@email.com"
        val password = "Password123"
        whenever(mockFirebaseAuth.signUp(email, password)).doReturn(Unit)

        userAuthRepositoryImpl.signUp(email, password)

        verify(mockFirebaseAuth).signUp(email, password)
    }

    @Test
    fun `sign in successfully`() = runTest {
        val email = "test@email.com"
        val password = "Password123"
        whenever(mockFirebaseAuth.signIn(email, password)).doReturn(Unit)

        userAuthRepositoryImpl.signIn(email, password)

        verify(mockFirebaseAuth).signIn(email, password)
    }

    @Test
    fun `sign out successfully`() = runTest {
        whenever(mockFirebaseAuth.signOut()).doAnswer(Answer {  })

        userAuthRepositoryImpl.signOut()

        verify(mockFirebaseAuth).signOut()
    }

    @Test
    fun `delete user successfully`() = runTest {
        whenever(mockFirebaseAuth.deleteCurrentUser()).doReturn(Unit)

        userAuthRepositoryImpl.deleteCurrentUser()

        verify(mockFirebaseAuth).deleteCurrentUser()
    }

    @Test
    fun `get current user id successfully`() = runTest {
        val userId = "t1e2s3t4"
        whenever(mockFirebaseAuth.getCurrentUserId()).doReturn(userId)

        val id = userAuthRepositoryImpl.getCurrentUserId()

        verify(mockFirebaseAuth).getCurrentUserId()
        assertEquals(userId, id)
    }

    @Test
    fun `re authenticate successfully`() = runTest {
        val email = "test@email.com"
        val password = "Password123"
        whenever(mockFirebaseAuth.reAuthenticate(email, password)).doReturn(Unit)

        userAuthRepositoryImpl.reAuthenticate(email, password)

        verify(mockFirebaseAuth).reAuthenticate(email, password)
    }
}