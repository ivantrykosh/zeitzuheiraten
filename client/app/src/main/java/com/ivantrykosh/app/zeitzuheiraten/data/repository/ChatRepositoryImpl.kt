package com.ivantrykosh.app.zeitzuheiraten.data.repository

import com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.firestore.FirestoreChats
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Chat
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.ChatRepository
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val firestoreChats: FirestoreChats,
) : ChatRepository {
    override suspend fun createChat(user1Id: String, user2Id: String, dateTime: Long): String {
        return firestoreChats.createChat(user1Id, user2Id, dateTime)
    }

    override suspend fun getChatByUsers(user1Id: String, user2Id: String): String? {
        return firestoreChats.getChatByUsers(user1Id, user2Id)
    }

    override suspend fun getChatsForUser(userId: String, startAfterLast: Boolean, pageSize: Int): List<Chat> {
        return firestoreChats.getChatsForUser(userId, startAfterLast, pageSize)
    }

}