package com.ivantrykosh.app.zeitzuheiraten.domain.repository

import com.ivantrykosh.app.zeitzuheiraten.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {

    suspend fun createMessage(chatId: String, message: Message)

    suspend fun getMessages(chatId: String, startAfterLast: Boolean, pageSize: Int): List<Message>

    fun observeMessages(chatId: String, afterDateTime: Long): Flow<List<Message>>
}