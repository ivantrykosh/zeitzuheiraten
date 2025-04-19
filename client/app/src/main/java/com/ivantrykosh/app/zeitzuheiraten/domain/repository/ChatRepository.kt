package com.ivantrykosh.app.zeitzuheiraten.domain.repository

import com.ivantrykosh.app.zeitzuheiraten.domain.model.Chat

interface ChatRepository {

    /**
     * @return chat id
     */
    suspend fun createChat(user1Id: String, user2Id: String, dateTime: Long): String

    /**
     * @return chatId if chat with userIds exists and null otherwise
     */
    suspend fun getChatByUsers(user1Id: String, user2Id: String): String?

    suspend fun getChatsForUser(userId: String, startAfterLast: Boolean, pageSize: Int): List<Chat>
}