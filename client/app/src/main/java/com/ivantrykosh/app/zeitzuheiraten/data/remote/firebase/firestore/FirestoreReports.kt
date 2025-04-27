package com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.firestore

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.ivantrykosh.app.zeitzuheiraten.domain.model.ReportUser
import com.ivantrykosh.app.zeitzuheiraten.utils.Collections
import kotlinx.coroutines.tasks.await

class FirestoreReports(private val firestore: FirebaseFirestore = Firebase.firestore) {

    suspend fun createReport(report: ReportUser) {
        val reportData = mapOf(
            ReportUser::userIdWhoReport.name to report.userIdWhoReport,
            ReportUser::reportedUserId.name to report.reportedUserId,
            ReportUser::dateTime.name to report.dateTime,
            ReportUser::description.name to report.description,
        )
        firestore.collection(Collections.REPORTS)
            .document()
            .set(reportData)
            .await()
    }
}