package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.messages

import com.ivantrykosh.app.zeitzuheiraten.data.repository.MessageRepositoryImpl
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Message
import kotlinx.coroutines.flow.Flow
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
class ObserveMessagesUseCaseTest {

    private lateinit var messageRepositoryImpl: MessageRepositoryImpl
    private lateinit var observeMessagesUseCase: ObserveMessagesUseCase

    @Before
    fun setup() {
        messageRepositoryImpl = mock()
        observeMessagesUseCase = ObserveMessagesUseCase(messageRepositoryImpl)
    }

    @Test
    fun `observe messages successfully`() = runBlocking {
        val afterDateTime = 20L
        val chatId = "chatId"
        val expectedFlow = mock<Flow<List<Message>>>()
        whenever(messageRepositoryImpl.observeMessages(chatId, afterDateTime)).doReturn(expectedFlow)

        val actualFlow = observeMessagesUseCase(chatId, afterDateTime)

        verify(messageRepositoryImpl).observeMessages(chatId, afterDateTime)
        Assert.assertEquals(expectedFlow, actualFlow)
    }
}