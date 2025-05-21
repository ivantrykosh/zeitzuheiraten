package com.ivantrykosh.app.zeitzuheiraten.data.repository

import com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.firestore.FirestoreChats
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Chat
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
class ChatRepositoryImplTest {

    private lateinit var mockFirestoreChats: FirestoreChats
    private lateinit var chatRepositoryImpl: ChatRepositoryImpl

    @Before
    fun setup() {
        mockFirestoreChats = mock()
        chatRepositoryImpl = ChatRepositoryImpl(mockFirestoreChats)
    }

    @Test
    fun `create chat successfully`() = runTest{
        val user1Id = "user1Id"
        val user2Id = "user2Id"
        val dateTime = 1L
        val expectedChatId = "chatId"
        whenever(mockFirestoreChats.createChat(user1Id, user2Id, dateTime)).doReturn(expectedChatId)

        val actualChatId = chatRepositoryImpl.createChat(user1Id, user2Id, dateTime)

        verify(mockFirestoreChats).createChat(user1Id, user2Id, dateTime)
        assertEquals(expectedChatId, actualChatId)
    }

    @Test
    fun `get chat by users successfully`() = runTest{
        val user1Id = "user1Id"
        val user2Id = "user2Id"
        val expectedChatId = "chatId"
        whenever(mockFirestoreChats.getChatByUsers(user1Id, user2Id)).doReturn(expectedChatId)

        val actualChatId = chatRepositoryImpl.getChatByUsers(user1Id, user2Id)

        verify(mockFirestoreChats).getChatByUsers(user1Id, user2Id)
        assertEquals(expectedChatId, actualChatId)
    }

    @Test
    fun `get chat for user successfully`() = runTest{
        val userId = "userId"
        val startAfterLast = false
        val pageSize = 10
        val expectedChats = listOf(Chat(), Chat())
        whenever(mockFirestoreChats.getChatsForUser(userId, startAfterLast, pageSize)).doReturn(expectedChats)

        val actualChats = chatRepositoryImpl.getChatsForUser(userId, startAfterLast, pageSize)

        verify(mockFirestoreChats).getChatsForUser(userId, startAfterLast, pageSize)
        assertEquals(expectedChats, actualChats)
    }
}