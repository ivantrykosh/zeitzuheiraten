package com.ivantrykosh.app.zeitzuheiraten.data.repository

import com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.firestore.FirestoreReports
import com.ivantrykosh.app.zeitzuheiraten.domain.model.ReportUser
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class ReportUserRepositoryImplTest {

    private lateinit var mockFirestoreReports: FirestoreReports
    private lateinit var reportUserRepositoryImpl: ReportUserRepositoryImpl

    @Before
    fun setup() {
        mockFirestoreReports = mock()
        reportUserRepositoryImpl = ReportUserRepositoryImpl(mockFirestoreReports)
    }

    @Test
    fun `create report successfully`() = runTest{
        val report = ReportUser()
        whenever(mockFirestoreReports.createReport(report)).doReturn(Unit)

        reportUserRepositoryImpl.createReport(report)

        verify(mockFirestoreReports).createReport(report)
    }
}