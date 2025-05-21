package com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.firestore

import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Chat
import com.ivantrykosh.app.zeitzuheiraten.utils.Collections
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.time.Instant

@RunWith(MockitoJUnitRunner::class)
class FirestoreChatsTest {

    private lateinit var mockFirestore: FirebaseFirestore
    private lateinit var mockCollectionReference: CollectionReference
    private lateinit var firestoreChats: FirestoreChats

    @Before
    fun setup() {
        mockCollectionReference = mock()
        mockFirestore = mock {
            on { it.collection(Collections.CHATS) } doReturn mockCollectionReference
        }
        firestoreChats = FirestoreChats(mockFirestore)
    }

    @Test
    fun `create chat successfully`() = runTest {
        val user1Id = "user1Id"
        val user2Id = "user2Id"
        val dateTime = Instant.now().toEpochMilli()
        val chatId = "chatId"
        val chat = mapOf(
            Chat::users.name to listOf(user1Id, user2Id),
            Chat::creationDateTime.name to dateTime,
        )
        val completedTask = Tasks.forResult<Void>(null)
        val documentRef = mock<DocumentReference> {
            on { it.id } doReturn chatId
            on { mockCollectionReference.document() } doReturn it
            on { it.set(any()) } doReturn completedTask
        }

        val newChatId = firestoreChats.createChat(user1Id, user2Id, dateTime)

        verify(mockFirestore).collection(Collections.CHATS)
        verify(mockCollectionReference).document()
        verify(documentRef).set(chat)
        assertEquals(chatId, newChatId)
    }

    @Test
    fun `get chat by users`() = runTest {
        val user1Id = "user1Id"
        val user2Id = "user2Id"
        val chatId = "chatId"
        val chat = Chat(id = chatId, users = listOf(user1Id, user2Id))
        val docSnapshot = mock<DocumentSnapshot> {
            onGeneric { it.toObject(Chat::class.java) } doReturn chat
            on { it.id } doReturn chatId
        }
        val querySnapshot = mock<QuerySnapshot> {
            onGeneric { it.documents } doReturn listOf(docSnapshot)
        }
        val completedTask = Tasks.forResult<QuerySnapshot>(querySnapshot)
        val query = mock<Query> {
            on { mockCollectionReference.whereArrayContains(Chat::users.name, user1Id) } doReturn it
            on { it.get() } doReturn completedTask
        }

        val idOfChat = firestoreChats.getChatByUsers(user1Id, user2Id)

        verify(mockFirestore).collection(Collections.CHATS)
        verify(mockCollectionReference).whereArrayContains(Chat::users.name, user1Id)
        verify(query).get()
        verify(docSnapshot).toObject(Chat::class.java)
        assertEquals(chatId, idOfChat)
    }

    @Test
    fun `get chats for user`() = runTest {
        val chatId = "chatId"
        val userId = "userId"
        val startAfterLast = false
        val pageSize = 10
        val chat = Chat(id = chatId)
        val docSnapshot = mock<DocumentSnapshot>()
        val queryDocSnapshot = mock<QueryDocumentSnapshot> {
            on { it.id } doReturn chatId
            onGeneric { it.toObject(Chat::class.java) } doReturn chat
        }
        val querySnapshot = mock<QuerySnapshot> {
            onGeneric { it.isEmpty } doReturn false
            onGeneric { it.documents } doReturn listOf(docSnapshot)
            on { it.size() } doReturn 1
            on { it.iterator() } doReturn mutableListOf(queryDocSnapshot).iterator()
        }
        val completedTask = Tasks.forResult<QuerySnapshot>(querySnapshot)
        val query = mock<Query> {
            on { mockCollectionReference.whereArrayContains(Chat::users.name, userId) } doReturn it
            on { it.orderBy(Chat::creationDateTime.name, Query.Direction.DESCENDING) } doReturn it
            on { it.limit(pageSize.toLong()) } doReturn it
            on { it.get() } doReturn completedTask
        }

        val chats = firestoreChats.getChatsForUser(userId, startAfterLast, pageSize)

        verify(mockFirestore).collection(Collections.CHATS)
        verify(mockCollectionReference).whereArrayContains(Chat::users.name, userId)
        verify(query).orderBy(Chat::creationDateTime.name, Query.Direction.DESCENDING)
        verify(query).limit(pageSize.toLong())
        verify(query).get()
        verify(queryDocSnapshot).toObject(Chat::class.java)
        assertEquals(1, chats.size)
        assertEquals(chatId, chats[0].id)
    }
}