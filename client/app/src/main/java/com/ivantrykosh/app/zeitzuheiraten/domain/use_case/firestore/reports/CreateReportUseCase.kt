package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.reports

import android.util.Log
import com.ivantrykosh.app.zeitzuheiraten.domain.model.ReportUser
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.ReportUserRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserAuthRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import java.time.Instant
import javax.inject.Inject

private const val LOG_TAG = "CreateReportUseCase"

class CreateReportUseCase @Inject constructor(
    private val userAuthRepository: UserAuthRepository,
    private val reportUserRepository: ReportUserRepository,
) {
    operator fun invoke(reportedUserId: String, description: String) = flow<Resource<Unit>> {
        try {
            emit(Resource.Loading())
            val userIdWhoReported = userAuthRepository.getCurrentUserId()
            val dateTime = Instant.now().toEpochMilli()
            val report = ReportUser(
                userIdWhoReport = userIdWhoReported,
                reportedUserId = reportedUserId,
                dateTime = dateTime,
                description = description,
            )
            reportUserRepository.createReport(report)
            emit(Resource.Success())
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message ?: "An error occurred")
            emit(Resource.Error(e))
        }
    }
}