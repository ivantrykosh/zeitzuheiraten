package com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.firestore

import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Feedback
import com.ivantrykosh.app.zeitzuheiraten.utils.Collections
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class FirestoreFeedbacksTest {

    private lateinit var mockFirestore: FirebaseFirestore
    private lateinit var mockCollectionReference: CollectionReference
    private lateinit var firestoreFeedbacks: FirestoreFeedbacks

    @Before
    fun setup() {
        mockCollectionReference = mock()
        mockFirestore = mock {
            on { it.collection(Collections.FEEDBACKS) } doReturn mockCollectionReference
        }
        firestoreFeedbacks = FirestoreFeedbacks(mockFirestore)
    }

    @Test
    fun `create feedback successfully`() = runTest {
        val feedback = Feedback(
            id = "feedbackId",
            userId = "userId",
            username = "username",
            postId = "postId",
            category = "Video",
            provider = "provider",
            rating = 5,
            description = "Description",
            date = 0L
        )
        val completedTask = Tasks.forResult<Void>(null)
        val documentRef = mock<DocumentReference> {
            on { mockCollectionReference.document() } doReturn it
            on { it.set(any()) } doReturn completedTask
        }

        firestoreFeedbacks.createFeedback(feedback)

        verify(mockFirestore).collection(Collections.FEEDBACKS)
        verify(mockCollectionReference).document()
        verify(documentRef).set(any())
    }

    @Test
    fun `get feedbacks for post successfully`() = runTest {
        val feedbackId = "feedbackId"
        val postId = "postId"
        val startAfterLast = false
        val pageSize = 10
        val feedback = Feedback()
        val docSnapshot = mock<DocumentSnapshot>()
        val queryDocSnapshot = mock<QueryDocumentSnapshot> {
            on { it.id } doReturn feedbackId
            onGeneric { it.toObject(Feedback::class.java) } doReturn feedback
        }
        val querySnapshot = mock<QuerySnapshot> {
            onGeneric { it.isEmpty } doReturn false
            onGeneric { it.documents } doReturn listOf(docSnapshot)
            on { it.size() } doReturn 1
            on { it.iterator() } doReturn mutableListOf(queryDocSnapshot).iterator()
        }
        val completedTask = Tasks.forResult<QuerySnapshot>(querySnapshot)
        val query = mock<Query> {
            on { mockCollectionReference.whereEqualTo(Feedback::postId.name, postId) } doReturn it
            on { it.orderBy(Feedback::date.name) } doReturn it
            on { it.limit(pageSize.toLong()) } doReturn it
            on { it.get() } doReturn completedTask
        }

        val feedbacks = firestoreFeedbacks.getFeedbacksForPost(postId, startAfterLast, pageSize)

        verify(mockFirestore).collection(Collections.FEEDBACKS)
        verify(mockCollectionReference).whereEqualTo(Feedback::postId.name, postId)
        verify(query).orderBy(Feedback::date.name)
        verify(query).limit(pageSize.toLong())
        verify(query).get()
        verify(queryDocSnapshot).toObject(Feedback::class.java)
        assertEquals(1, feedbacks.size)
        assertEquals(feedbackId, feedbacks[0].id)
    }

    @Test
    fun `get bookings for user successfully`() = runTest {
        val feedbackId = "feedbackId"
        val userId = "userId"
        val startAfterLast = false
        val pageSize = 10
        val feedback = Feedback()
        val docSnapshot = mock<DocumentSnapshot>()
        val queryDocSnapshot = mock<QueryDocumentSnapshot> {
            on { it.id } doReturn feedbackId
            onGeneric { it.toObject(Feedback::class.java) } doReturn feedback
        }
        val querySnapshot = mock<QuerySnapshot> {
            onGeneric { it.isEmpty } doReturn false
            onGeneric { it.documents } doReturn listOf(docSnapshot)
            on { it.size() } doReturn 1
            on { it.iterator() } doReturn mutableListOf(queryDocSnapshot).iterator()
        }
        val completedTask = Tasks.forResult<QuerySnapshot>(querySnapshot)
        val query = mock<Query> {
            on { mockCollectionReference.whereEqualTo(Feedback::userId.name, userId) } doReturn it
            on { it.orderBy(Feedback::date.name) } doReturn it
            on { it.limit(pageSize.toLong()) } doReturn it
            on { it.get() } doReturn completedTask
        }

        val feedbacks = firestoreFeedbacks.getFeedbacksForUser(userId, startAfterLast, pageSize)

        verify(mockFirestore).collection(Collections.FEEDBACKS)
        verify(mockCollectionReference).whereEqualTo(Feedback::userId.name, userId)
        verify(query).orderBy(Feedback::date.name)
        verify(query).limit(pageSize.toLong())
        verify(query).get()
        verify(queryDocSnapshot).toObject(Feedback::class.java)
        assertEquals(1, feedbacks.size)
        assertEquals(feedbackId, feedbacks[0].id)
    }

    @Test
    fun `delete feedback successfully`() = runTest {
        val feedbackId = "feedbackId"
        val completedTask = Tasks.forResult<Void>(null)
        val documentRef = mock<DocumentReference> {
            on { mockCollectionReference.document(feedbackId) } doReturn it
            on { it.delete() } doReturn completedTask
        }

        firestoreFeedbacks.deleteFeedback(feedbackId)

        verify(mockFirestore).collection(Collections.FEEDBACKS)
        verify(mockCollectionReference).document(feedbackId)
        verify(documentRef).delete()
    }
}