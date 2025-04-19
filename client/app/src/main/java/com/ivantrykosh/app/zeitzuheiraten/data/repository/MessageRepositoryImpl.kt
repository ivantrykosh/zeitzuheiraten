package com.ivantrykosh.app.zeitzuheiraten.data.repository

import com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.firestore.FirestoreMessages
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Message
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
    private val firestoreMessages: FirestoreMessages,
) : MessageRepository {

    override suspend fun createMessage(chatId: String, message: Message) {
        firestoreMessages.createMessage(chatId, message)
    }

    override suspend fun getMessages(chatId: String, startAfterLast: Boolean, pageSize: Int): List<Message> {
        return firestoreMessages.getMessages(chatId, startAfterLast, pageSize)
    }

    override fun observeMessages(chatId: String, afterDateTime: Long): Flow<List<Message>> {
        return firestoreMessages.observeMessages(chatId, afterDateTime)
    }
}