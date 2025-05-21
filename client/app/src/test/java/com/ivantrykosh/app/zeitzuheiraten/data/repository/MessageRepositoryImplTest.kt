package com.ivantrykosh.app.zeitzuheiraten.data.repository

import com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.firestore.FirestoreMessages
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Message
import kotlinx.coroutines.flow.Flow
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
class MessageRepositoryImplTest {

    private lateinit var mockFirestoreMessages: FirestoreMessages
    private lateinit var messageRepositoryImpl: MessageRepositoryImpl

    @Before
    fun setup() {
        mockFirestoreMessages = mock()
        messageRepositoryImpl = MessageRepositoryImpl(mockFirestoreMessages)
    }

    @Test
    fun `create message successfully`() = runTest{
        val chatId = "chatId"
        val message = Message()
        whenever(mockFirestoreMessages.createMessage(chatId, message)).doReturn(Unit)

        messageRepositoryImpl.createMessage(chatId, message)

        verify(mockFirestoreMessages).createMessage(chatId, message)
    }

    @Test
    fun `get messages successfully`() = runTest{
        val chatId = "chatId"
        val startAfterLast = false
        val pageSize = 20
        val expectedMessages = listOf(Message(), Message())
        whenever(mockFirestoreMessages.getMessages(chatId, startAfterLast, pageSize)).doReturn(expectedMessages)

        val actualMessages = messageRepositoryImpl.getMessages(chatId, startAfterLast, pageSize)

        verify(mockFirestoreMessages).getMessages(chatId, startAfterLast, pageSize)
        assertEquals(expectedMessages, actualMessages)
    }

    @Test
    fun `observe messages successfully`() = runTest{
        val chatId = "chatId"
        val afterDateTime = 10L
        val expectedFlow = mock<Flow<List<Message>>>()
        whenever(mockFirestoreMessages.observeMessages(chatId, afterDateTime)).doReturn(expectedFlow)

        val actualFlow = messageRepositoryImpl.observeMessages(chatId, afterDateTime)

        verify(mockFirestoreMessages).observeMessages(chatId, afterDateTime)
        assertEquals(expectedFlow, actualFlow)
    }
}