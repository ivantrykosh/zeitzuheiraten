package com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.firestore

import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.ivantrykosh.app.zeitzuheiraten.domain.model.ReportUser
import com.ivantrykosh.app.zeitzuheiraten.utils.Collections
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class FirestoreReportsTest {

    private lateinit var mockFirestore: FirebaseFirestore
    private lateinit var mockCollectionReference: CollectionReference
    private lateinit var firestoreReports: FirestoreReports

    @Before
    fun setup() {
        mockCollectionReference = mock()
        mockFirestore = mock {
            on { it.collection(Collections.REPORTS) } doReturn mockCollectionReference
        }
        firestoreReports = FirestoreReports(mockFirestore)
    }

    @Test
    fun `create report successfully`() = runTest {
        val report = ReportUser(
            userIdWhoReport = "user1Id",
            reportedUserId = "user2Id",
            dateTime = 100000000000L,
            description = "Description"
        )
        val completedTask = Tasks.forResult<Void>(null)
        val documentRef = mock<DocumentReference> {
            on { mockCollectionReference.document() } doReturn it
            on { it.set(any()) } doReturn completedTask
        }

        firestoreReports.createReport(report)

        verify(mockFirestore).collection(Collections.REPORTS)
        verify(mockCollectionReference).document()
        verify(documentRef).set(any())
    }
}