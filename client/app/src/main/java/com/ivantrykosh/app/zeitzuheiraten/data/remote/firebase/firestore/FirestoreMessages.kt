package com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.firestore

import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query.Direction
import com.google.firebase.firestore.firestore
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Message
import com.ivantrykosh.app.zeitzuheiraten.utils.Collections
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreMessages(private val firestore: FirebaseFirestore = Firebase.firestore) {

    private lateinit var lastVisibleMessage: DocumentSnapshot

    suspend fun createMessage(chatId: String, message: Message) {
        val messageData = mapOf(
            Message::message.name to message.message,
            Message::senderId.name to message.senderId,
            Message::dateTime.name to message.dateTime,
        )
        firestore
            .collection(Collections.CHATS).document(chatId)
            .collection(Collections.MESSAGES).document()
            .set(messageData)
            .await()
    }

    suspend fun getMessages(chatId: String, startAfterLast: Boolean, pageSize: Int): List<Message> {
        return firestore
            .collection(Collections.CHATS).document(chatId)
            .collection(Collections.MESSAGES)
            .orderBy(Message::dateTime.name, Direction.DESCENDING)
            .let {
                if (startAfterLast) {
                    it.startAfter(lastVisibleMessage)
                } else {
                    it
                }
            }
            .limit(pageSize.toLong())
            .get()
            .await()
            .also {
                if (!it.isEmpty) {
                    lastVisibleMessage = it.documents[it.size() - 1]
                }
            }
            .map { doc ->
                doc.toObject(Message::class.java).copy(id = doc.id)
            }
    }

    fun observeMessages(chatId: String, afterDateTime: Long): Flow<List<Message>> {
        return callbackFlow {
            val listener = firestore
                .collection(Collections.CHATS).document(chatId)
                .collection(Collections.MESSAGES)
                .orderBy(Message::dateTime.name, Direction.DESCENDING)
                .whereGreaterThan(Message::dateTime.name, afterDateTime)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val newMessages = snapshot?.documents?.mapNotNull { doc ->
                        doc.toObject(Message::class.java)?.copy(id = doc.id)
                    } ?: emptyList()
                    trySend(newMessages)
                }
            awaitClose { listener.remove() }
        }
    }
}