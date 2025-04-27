package com.ivantrykosh.app.zeitzuheiraten.domain.repository

import com.ivantrykosh.app.zeitzuheiraten.domain.model.ReportUser

interface ReportUserRepository {

    suspend fun createReport(report: ReportUser)
}