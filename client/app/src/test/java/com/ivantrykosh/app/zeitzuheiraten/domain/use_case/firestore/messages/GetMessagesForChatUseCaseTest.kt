package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.messages

import com.ivantrykosh.app.zeitzuheiraten.data.repository.MessageRepositoryImpl
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
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class GetMessagesForChatUseCaseTest {

    private lateinit var messageRepositoryImpl: MessageRepositoryImpl
    private lateinit var getMessagesForChatUseCase: GetMessagesForChatUseCase

    @Before
    fun setup() {
        messageRepositoryImpl = mock()
        getMessagesForChatUseCase = GetMessagesForChatUseCase(messageRepositoryImpl)
    }

    @Test
    fun `get messages for chat successfully`() = runBlocking {
        val startAfterLast = false
        val pageSize = 20
        val chatId = "chatId"
        var resourceSuccess = false
        var actualMessages = listOf<Message>()
        val expectedMessages = listOf<Message>(Message())
        whenever(messageRepositoryImpl.getMessages(chatId, startAfterLast, pageSize)).doReturn(expectedMessages)

        getMessagesForChatUseCase(chatId, startAfterLast, pageSize).collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { Assert.fail(result.error.message) }
                is Resource.Success -> {
                    resourceSuccess = true
                    actualMessages = result.data!!
                }
            }
        }

        verify(messageRepositoryImpl).getMessages(chatId, startAfterLast, pageSize)
        Assert.assertTrue(resourceSuccess)
        Assert.assertEquals(expectedMessages, actualMessages)
    }

    @Test(expected = CancellationException::class)
    fun `get messages for chat first emit must be loading`() = runBlocking {
        val startAfterLast = false
        val pageSize = 20
        val postId = "postId"

        getMessagesForChatUseCase(postId, startAfterLast, pageSize).collect { result ->
            when (result) {
                is Resource.Loading -> { this.cancel() }
                is Resource.Error -> { Assert.fail("Loading must be first") }
                is Resource.Success -> { Assert.fail("Loading must be first") }
            }
        }
    }
}