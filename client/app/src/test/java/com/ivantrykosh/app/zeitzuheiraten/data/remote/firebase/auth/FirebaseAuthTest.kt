package com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.auth

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
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
class FirebaseAuthTest {

    private lateinit var mockFirebaseAuth: FirebaseAuth
    private lateinit var auth: com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.auth.FirebaseAuth

    @Before
    fun setup() {
        mockFirebaseAuth = mock()
        auth = FirebaseAuth(mockFirebaseAuth)
    }

    @Test
    fun `sign up successfully`() = runTest {
        val email = "test@email.com"
        val password = "Password123"
        val completedTask: Task<AuthResult> = Tasks.forResult(null)
        mock<Task<AuthResult>> {
            on { mockFirebaseAuth.createUserWithEmailAndPassword(email, password) } doReturn completedTask
        }

        auth.signUp(email, password)

        verify(mockFirebaseAuth).createUserWithEmailAndPassword(email, password)
    }

    @Test
    fun `sign in successfully`() = runTest {
        val email = "test@email.com"
        val password = "Password123"
        val completedTask: Task<AuthResult> = Tasks.forResult(null)
        mock<Task<AuthResult>> {
            on { mockFirebaseAuth.signInWithEmailAndPassword(email, password) } doReturn completedTask
        }

        auth.signIn(email, password)

        verify(mockFirebaseAuth).signInWithEmailAndPassword(email, password)
    }

    @Test
    fun `sign out successfully`() = runTest {
        auth.signOut()

        verify(mockFirebaseAuth).signOut()
    }

    @Test
    fun `reset password successfully`() = runTest {
        val email = "test@email.com"
        val completedTask: Task<Void> = Tasks.forResult(null)
        mock<Task<Void>> {
            on { mockFirebaseAuth.sendPasswordResetEmail(email) } doReturn completedTask
        }

        auth.resetPassword(email)

        verify(mockFirebaseAuth).sendPasswordResetEmail(email)
    }

    @Test
    fun `delete current user successfully`() = runTest {
        val mockUser: FirebaseUser = mock()
        val completedTask: Task<Void> = Tasks.forResult(null)
        mock<Task<Void>> {
            on { mockFirebaseAuth.currentUser } doReturn mockUser
            on { mockUser.delete() } doReturn completedTask
        }

        auth.deleteCurrentUser()

        verify(mockUser).delete()
    }

    @Test(expected = NullPointerException::class)
    fun `delete current user failed because user is not logged in`() = runTest {
        val user: FirebaseUser? = null
        mock<Task<Void>> {
            on { mockFirebaseAuth.currentUser } doReturn user
        }

        auth.deleteCurrentUser()
    }

    @Test
    fun `get current user id when user is logged in`() = runTest {
        val userUid = "t1e2s3t4"
        mock<FirebaseUser> {
            on { mockFirebaseAuth.currentUser } doReturn it
            on { it.uid } doReturn userUid
        }

        val uid = auth.getCurrentUserId()

        assertEquals(userUid, uid)
    }

    @Test
    fun `get current user id when user is not logged in`() = runTest {
        val userUid = ""
        mock<FirebaseUser> {
            on { mockFirebaseAuth.currentUser } doReturn null
        }

        val uid = auth.getCurrentUserId()

        verify(mockFirebaseAuth).currentUser
        assertEquals(userUid, uid)
    }

    @Test
    fun `send verification email successfully`() = runTest {
        val completedTask = Tasks.forResult<Void>(null)
        val user = mock<FirebaseUser> {
            on { mockFirebaseAuth.currentUser } doReturn it
            on { it.sendEmailVerification() } doReturn completedTask
        }

        auth.sendVerificationEmail()

        verify(mockFirebaseAuth).currentUser
        verify(user).sendEmailVerification()
    }

    @Test
    fun `is email verified returns true because it is verified`() = runTest {
        mock<FirebaseUser> {
            on { mockFirebaseAuth.currentUser } doReturn it
            on { it.isEmailVerified } doReturn true
        }

        val isVerified = auth.isEmailVerified()

        verify(mockFirebaseAuth).currentUser
        assertTrue(isVerified)
    }

    @Test
    fun `is email verified returns false because it is not verified`() = runTest {
        mock<FirebaseUser> {
            on { mockFirebaseAuth.currentUser } doReturn it
            on { it.isEmailVerified } doReturn false
        }

        val isVerified = auth.isEmailVerified()

        verify(mockFirebaseAuth).currentUser
        assertFalse(isVerified)
    }
}