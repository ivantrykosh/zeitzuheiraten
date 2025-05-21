package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.messages

import com.ivantrykosh.app.zeitzuheiraten.data.repository.ChatRepositoryImpl
import com.ivantrykosh.app.zeitzuheiraten.data.repository.MessageRepositoryImpl
import com.ivantrykosh.app.zeitzuheiraten.data.repository.UserAuthRepositoryImpl
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Message
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class CreateMessageUseCaseTest {

    private lateinit var userAuthRepositoryImpl: UserAuthRepositoryImpl
    private lateinit var chatRepositoryImpl: ChatRepositoryImpl
    private lateinit var messageRepositoryImpl: MessageRepositoryImpl
    private lateinit var createMessageUseCase: CreateMessageUseCase

    @Before
    fun setup() {
        userAuthRepositoryImpl = mock()
        chatRepositoryImpl = mock()
        messageRepositoryImpl = mock()
        createMessageUseCase = CreateMessageUseCase(userAuthRepositoryImpl, chatRepositoryImpl, messageRepositoryImpl)
    }

    @Test
    fun `create message successfully`() = runBlocking {
        val chatId = "chatId"
        val message = "test"
        val otherUserId: String? = null

        val userId = "userId"
        var resourceSuccess = false
        whenever(userAuthRepositoryImpl.getCurrentUserId()).doReturn(userId)
        whenever(messageRepositoryImpl.createMessage(eq(chatId), any<Message>())).doReturn(Unit)

        createMessageUseCase(chatId, message, otherUserId).collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { Assert.fail(result.error.message) }
                is Resource.Success -> { resourceSuccess = true }
            }
        }

        verify(userAuthRepositoryImpl).getCurrentUserId()
        verify(messageRepositoryImpl).createMessage(eq(chatId), any<Message>())
        Assert.assertTrue(resourceSuccess)
    }

    @Test
    fun `create message and chat successfully`() = runBlocking {
        val chatId: String? = null
        val newChatId = "chatId"
        val message = "test"
        val otherUserId = "user2Id"

        val userId = "userId"
        var resourceSuccess = false
        whenever(userAuthRepositoryImpl.getCurrentUserId()).doReturn(userId)
        whenever(chatRepositoryImpl.createChat(eq(userId), eq(otherUserId), any())).doReturn(newChatId)
        whenever(messageRepositoryImpl.createMessage(eq(newChatId), any<Message>())).doReturn(Unit)

        createMessageUseCase(chatId, message, otherUserId).collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { Assert.fail(result.error.message) }
                is Resource.Success -> { resourceSuccess = true }
            }
        }

        verify(userAuthRepositoryImpl).getCurrentUserId()
        verify(chatRepositoryImpl).createChat(eq(userId), eq(otherUserId), any())
        verify(messageRepositoryImpl).createMessage(eq(newChatId), any<Message>())
        Assert.assertTrue(resourceSuccess)
    }

    @Test(expected = CancellationException::class)
    fun `create message first emit must be loading`() = runBlocking {
        val chatId = "chatId"
        val message = "test"
        val otherUserId: String? = null

        createMessageUseCase(chatId, message, otherUserId).collect { result ->
            when (result) {
                is Resource.Loading -> { this.cancel() }
                is Resource.Error -> { Assert.fail("Loading must be first") }
                is Resource.Success -> { Assert.fail("Loading must be first") }
            }
        }
    }
}