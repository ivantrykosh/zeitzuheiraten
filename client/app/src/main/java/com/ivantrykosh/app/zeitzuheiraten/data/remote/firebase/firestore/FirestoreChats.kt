package com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.firestore

import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Chat
import com.ivantrykosh.app.zeitzuheiraten.utils.Collections
import kotlinx.coroutines.tasks.await

class FirestoreChats(private val firestore: FirebaseFirestore = Firebase.firestore) {

    private lateinit var lastVisibleChat: DocumentSnapshot

    suspend fun createChat(user1Id: String, user2Id: String, dateTime: Long): String {
        val chat = mapOf(
            Chat::users.name to listOf(user1Id, user2Id),
            Chat::creationDateTime.name to dateTime,
        )
        val chatId: String
        firestore.collection(Collections.CHATS)
            .document()
            .also { chatId = it.id }
            .set(chat)
            .await()
        return chatId
    }

    suspend fun getChatByUsers(user1Id: String, user2Id: String): String? {
        val snapshot = firestore.collection(Collections.CHATS)
            .whereArrayContains(Chat::users.name, user1Id)
            .get()
            .await()

        val chat = snapshot.documents.firstOrNull { doc ->
            val chat = doc.toObject(Chat::class.java)!!
            chat.users.contains(user2Id)
        }

        return chat?.id
    }

    suspend fun getChatsForUser(userId: String, startAfterLast: Boolean, pageSize: Int): List<Chat> {
        return firestore.collection(Collections.CHATS)
            .whereArrayContains(Chat::users.name, userId)
            .orderBy(Chat::creationDateTime.name) // order by time of last message
            .let {
                if (startAfterLast) {
                    it.startAfter(lastVisibleChat)
                } else {
                    it
                }
            }
            .limit(pageSize.toLong())
            .get()
            .await()
            .also {
                if (!it.isEmpty) {
                    lastVisibleChat = it.documents[it.size() - 1]
                }
            }
            .map { doc ->
                doc.toObject(Chat::class.java).copy(id = doc.id)
            }
    }
}