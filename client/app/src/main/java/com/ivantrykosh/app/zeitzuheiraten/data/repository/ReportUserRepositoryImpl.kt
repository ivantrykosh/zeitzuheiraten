package com.ivantrykosh.app.zeitzuheiraten.data.repository

import com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.firestore.FirestoreReports
import com.ivantrykosh.app.zeitzuheiraten.domain.model.ReportUser
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.ReportUserRepository
import javax.inject.Inject

class ReportUserRepositoryImpl @Inject constructor(
    private val firestoreReports: FirestoreReports,
) : ReportUserRepository {
    override suspend fun createReport(report: ReportUser) {
        firestoreReports.createReport(report)
    }
}