package com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.firestore

import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Query.Direction
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Message
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

@RunWith(MockitoJUnitRunner::class)
class FirestoreMessagesTest {

    private lateinit var mockFirestore: FirebaseFirestore
    private lateinit var mockChatsCollectionReference: CollectionReference
    private lateinit var mockMessagesCollectionReference: CollectionReference
    private lateinit var firestoreMessages: FirestoreMessages

    @Before
    fun setup() {
        mockChatsCollectionReference = mock()
        mockMessagesCollectionReference = mock()
        mockFirestore = mock {
            on { it.collection(Collections.CHATS) } doReturn mockChatsCollectionReference
        }
        firestoreMessages = FirestoreMessages(mockFirestore)
    }

    @Test
    fun `create message successfully`() = runTest {
        val chatId = "chatId"
        val message = Message(
            message = "Test",
            senderId = "senderId",
            dateTime = 17000954302403L,
        )
        val completedTask = Tasks.forResult<Void>(null)
        val documentRef = mock<DocumentReference> {
            on { mockChatsCollectionReference.document(chatId) } doReturn it
            on { it.collection(Collections.MESSAGES) } doReturn mockMessagesCollectionReference
            on { mockMessagesCollectionReference.document() } doReturn it
            on { it.set(any()) } doReturn completedTask
        }

        firestoreMessages.createMessage(chatId, message)

        verify(mockFirestore).collection(Collections.CHATS)
        verify(mockChatsCollectionReference).document(chatId)
        verify(documentRef).collection(Collections.MESSAGES)
        verify(mockMessagesCollectionReference).document()
        verify(documentRef).set(any())
    }

    @Test
    fun `get messages for post successfully`() = runTest {
        val chatId = "chatId"
        val messageId = "messageId"
        val startAfterLast = false
        val pageSize = 10
        val message = Message()
        val docSnapshot = mock<DocumentSnapshot>()
        val queryDocSnapshot = mock<QueryDocumentSnapshot> {
            on { it.id } doReturn messageId
            onGeneric { it.toObject(Message::class.java) } doReturn message
        }
        val querySnapshot = mock<QuerySnapshot> {
            onGeneric { it.isEmpty } doReturn false
            onGeneric { it.documents } doReturn listOf(docSnapshot)
            on { it.size() } doReturn 1
            on { it.iterator() } doReturn mutableListOf(queryDocSnapshot).iterator()
        }
        val completedTask = Tasks.forResult<QuerySnapshot>(querySnapshot)
        val mockChat = mock<DocumentReference> {
            on { mockChatsCollectionReference.document(chatId) } doReturn it
            on { it.collection(Collections.MESSAGES) } doReturn mockMessagesCollectionReference
        }
        val query = mock<Query> {
            on { mockMessagesCollectionReference.orderBy(Message::dateTime.name, Direction.DESCENDING) } doReturn it
            on { it.limit(pageSize.toLong()) } doReturn it
            on { it.get() } doReturn completedTask
        }

        val messages = firestoreMessages.getMessages(chatId, startAfterLast, pageSize)

        verify(mockFirestore).collection(Collections.CHATS)
        verify(mockChatsCollectionReference).document(chatId)
        verify(mockChat).collection(Collections.MESSAGES)
        verify(mockMessagesCollectionReference).orderBy(Message::dateTime.name, Direction.DESCENDING)
        verify(query).limit(pageSize.toLong())
        verify(query).get()
        verify(queryDocSnapshot).toObject(Message::class.java)
        assertEquals(1, messages.size)
        assertEquals(messageId, messages[0].id)
    }
}