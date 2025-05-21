package com.ivantrykosh.app.zeitzuheiraten.data.repository

import com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.firestore.FirestoreUsers
import com.ivantrykosh.app.zeitzuheiraten.domain.model.User
import kotlinx.coroutines.test.runTest
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
class UserRepositoryImplTest {

    private lateinit var mockFirestoreUsers: FirestoreUsers
    private lateinit var userRepositoryImpl: UserRepositoryImpl

    @Before
    fun setup() {
        mockFirestoreUsers = mock()
        userRepositoryImpl = UserRepositoryImpl(mockFirestoreUsers)
    }

    @Test
    fun `create user successfully`() = runTest{
        val user = User(id = "u1s2e3r4", name = "Test User", email = "test@email.com")
        whenever(mockFirestoreUsers.createUser(user)).doReturn(Unit)

        userRepositoryImpl.createUser(user)

        verify(mockFirestoreUsers).createUser(user)
    }

    @Test
    fun `get user by id successfully`() = runTest{
        val userId = "u1s2e3r4"
        val testUser = User(id = userId, name = "Test User", email = "test@email.com")
        whenever(mockFirestoreUsers.getUserById(userId)).doReturn(testUser)

        val user = userRepositoryImpl.getUserById(userId)

        verify(mockFirestoreUsers).getUserById(userId)
        assertEquals(userId, user?.id)
        assertEquals(testUser.email, user?.email)
        assertEquals(testUser.name, user?.name)
    }

    @Test
    fun `update user successfully`() = runTest{
        val user = User(id = "u1s2e3r4", name = "Test User", email = "test@email.com")
        whenever(mockFirestoreUsers.updateUser(user)).doReturn(Unit)

        userRepositoryImpl.updateUser(user)

        verify(mockFirestoreUsers).updateUser(user)
    }

    @Test
    fun `delete user by id successfully`() = runTest{
        val userId = "u1s2e3r4"
        whenever(mockFirestoreUsers.deleteUser(userId)).doReturn(Unit)

        userRepositoryImpl.deleteUser(userId)

        verify(mockFirestoreUsers).deleteUser(userId)
    }
}