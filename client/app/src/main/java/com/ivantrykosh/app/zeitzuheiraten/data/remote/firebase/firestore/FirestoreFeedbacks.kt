package com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.firestore

import com.google.firebase.Firebase
import com.google.firebase.firestore.AggregateField
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Feedback
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Rating
import com.ivantrykosh.app.zeitzuheiraten.utils.Collections
import kotlinx.coroutines.tasks.await

class FirestoreFeedbacks(private val firestore: FirebaseFirestore = Firebase.firestore) {

    private lateinit var lastVisibleFeedback: DocumentSnapshot

    suspend fun createFeedback(feedback: Feedback) {
        val feedbackData = mapOf(
            Feedback::userId.name to feedback.userId,
            Feedback::username.name to feedback.username,
            Feedback::postId.name to feedback.postId,
            Feedback::rating.name to feedback.rating,
            Feedback::description.name to feedback.description,
            Feedback::date.name to feedback.date,
        )
        firestore.collection(Collections.FEEDBACKS)
            .document()
            .set(feedbackData)
            .await()
    }

    suspend fun getRatingForPost(postId: String): Rating {
        val result = firestore.collection(Collections.FEEDBACKS)
            .whereEqualTo(Feedback::postId.name, postId)
            .aggregate(AggregateField.average(Feedback::rating.name), AggregateField.count())
            .get(AggregateSource.SERVER)
            .await()
        return Rating(
            result.get(AggregateField.average(Feedback::rating.name))!!,
            result.get(AggregateField.count()),
        )
    }

    suspend fun getFeedbacksForPost(postId: String, startAfterLast: Boolean, pageSize: Int): List<Feedback> {
        return firestore.collection(Collections.FEEDBACKS)
            .whereEqualTo(Feedback::postId.name, postId)
            .orderBy(Feedback::date.name)
            .let {
                if (startAfterLast) {
                    it.startAfter(lastVisibleFeedback)
                } else {
                    it
                }
            }
            .limit(pageSize.toLong())
            .get()
            .await()
            .also {
                if (!it.isEmpty) {
                    lastVisibleFeedback = it.documents[it.size() - 1]
                }
            }
            .map { doc ->
                doc.toObject(Feedback::class.java).copy(id = doc.id)
            }
    }

    suspend fun getFeedbacksForUser(userId: String, startAfterLast: Boolean, pageSize: Int): List<Feedback> {
        return firestore.collection(Collections.FEEDBACKS)
            .whereEqualTo(Feedback::userId.name, userId)
            .orderBy(Feedback::date.name)
            .let {
                if (startAfterLast) {
                    it.startAfter(lastVisibleFeedback)
                } else {
                    it
                }
            }
            .limit(pageSize.toLong())
            .get()
            .await()
            .also {
                if (!it.isEmpty) {
                    lastVisibleFeedback = it.documents[it.size() - 1]
                }
            }
            .map { doc ->
                doc.toObject(Feedback::class.java).copy(id = doc.id)
            }
    }

    suspend fun deleteFeedback(id: String) {
        firestore.collection(Collections.FEEDBACKS)
            .document(id)
            .delete()
            .await()
    }

    suspend fun deleteFeedbacksForPost(postId: String) {
        firestore.collection(Collections.FEEDBACKS)
            .whereEqualTo(Feedback::postId.name, postId)
            .get()
            .await()
            .documents
            .forEach {
                it.reference.delete().await()
            }
    }

    suspend fun deleteFeedbacksForUser(userId: String) {
        firestore.collection(Collections.FEEDBACKS)
            .whereEqualTo(Feedback::userId.name, userId)
            .get()
            .await()
            .documents
            .forEach {
                it.reference.delete().await()
            }
    }
}