package com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.firestore

import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.ivantrykosh.app.zeitzuheiraten.domain.model.User
import com.ivantrykosh.app.zeitzuheiraten.utils.Collections
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class FirestoreUsersTest {

    private lateinit var mockFirestore: FirebaseFirestore
    private lateinit var mockCollectionReference: CollectionReference
    private lateinit var firestoreUsers: FirestoreUsers

    @Before
    fun setup() {
        mockCollectionReference = mock()
        mockFirestore = mock {
            on { it.collection(Collections.USERS) } doReturn mockCollectionReference
        }
        firestoreUsers = FirestoreUsers(mockFirestore)
    }

    @Test
    fun `create user successfully`() = runTest {
        val userId = "u1s2e3r4"
        val user = User(id = userId, name = "Test User", email = "test@email.com")
        val userData = mapOf(
            User::name.name to user.name,
            User::email.name to user.email,
            User::imageUrl.name to user.imageUrl,
            User::isProvider.name to user.isProvider,
            User::creationTime.name to user.creationTime,
            User::lastUsernameChange.name to user.lastUsernameChange,
        )
        val completedTask = Tasks.forResult<Void>(null)
        val documentRef = mock<DocumentReference> {
            on { mockCollectionReference.document(userId) } doReturn it
            on { it.set(userData) } doReturn completedTask
        }

        firestoreUsers.createUser(user)

        verify(mockFirestore).collection(Collections.USERS)
        verify(mockCollectionReference).document(userId)
        verify(documentRef).set(userData)
    }

    @Test
    fun `get user by ID successfully`() = runTest {
        val userId = "u1s2e3r4"
        val testUser = User(id = userId, name = "Test User", email = "test@email.com")
        val documentSnapshot = mock<DocumentSnapshot> {
            onGeneric { it.toObject(User::class.java) } doReturn testUser
        }
        val completedTask = Tasks.forResult(documentSnapshot)
        val documentRef = mock<DocumentReference> {
            on { mockCollectionReference.document(userId) } doReturn it
            on { it.get() } doReturn completedTask
        }

        val user = firestoreUsers.getUserById(userId)

        verify(mockFirestore).collection(Collections.USERS)
        verify(mockCollectionReference).document(userId)
        verify(documentRef).get()
        assertEquals(userId, user?.id)
        assertEquals(testUser.email, user?.email)
        assertEquals(testUser.name, user?.name)
    }

    @Test
    fun `get user by ID failed because user does not exist`() = runTest {
        val userId = "wrongId"
        val documentSnapshot = mock<DocumentSnapshot> {
            onGeneric { it.toObject(User::class.java) }.thenReturn(null as User?)
        }
        val completedTask = Tasks.forResult(documentSnapshot)
        mock<DocumentReference> {
            on { mockCollectionReference.document(userId) } doReturn it
            on { it.get() } doReturn completedTask
        }

        val user = firestoreUsers.getUserById(userId)

        assertNull(user)
    }

    @Test
    fun `update user successfully`() = runTest {
        val user = User(id = "u1s2e3r4", name = "Test User", email = "test@email.com")
        val userData = mapOf(
            User::name.name to user.name,
            User::imageUrl.name to user.imageUrl,
            User::lastUsernameChange.name to user.lastUsernameChange
        )
        val completedTask = Tasks.forResult<Void>(null)
        val documentRef = mock<DocumentReference> {
            on { mockCollectionReference.document(user.id) } doReturn it
            on { it.update(userData) } doReturn completedTask
        }

        firestoreUsers.updateUser(user)

        verify(mockFirestore).collection(Collections.USERS)
        verify(mockCollectionReference).document(user.id)
        verify(documentRef).update(userData)
    }

    @Test
    fun `delete user successfully`() = runTest {
        val userId = "u1s2e3r4"
        val completedTask = Tasks.forResult<Void>(null)
        val documentRef = mock<DocumentReference> {
            on { mockCollectionReference.document(userId) } doReturn it
            on { it.delete() } doReturn completedTask
        }

        firestoreUsers.deleteUser(userId)

        verify(mockFirestore).collection(Collections.USERS)
        verify(mockCollectionReference).document(userId)
        verify(documentRef).delete()
    }
}