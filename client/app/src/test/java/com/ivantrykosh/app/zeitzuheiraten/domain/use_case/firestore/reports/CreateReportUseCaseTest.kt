package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.reports

import com.ivantrykosh.app.zeitzuheiraten.data.repository.ReportUserRepositoryImpl
import com.ivantrykosh.app.zeitzuheiraten.data.repository.UserAuthRepositoryImpl
import com.ivantrykosh.app.zeitzuheiraten.domain.model.ReportUser
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class CreateReportUseCaseTest {

    private lateinit var userAuthRepositoryImpl: UserAuthRepositoryImpl
    private lateinit var reportUserRepositoryImpl: ReportUserRepositoryImpl
    private lateinit var createReportUseCase: CreateReportUseCase

    @Before
    fun setup() {
        userAuthRepositoryImpl = mock()
        reportUserRepositoryImpl = mock()
        createReportUseCase = CreateReportUseCase(userAuthRepositoryImpl, reportUserRepositoryImpl)
    }

    @Test
    fun `create report successfully`() = runBlocking {
        val reportedUserId = "reportedUserId"
        val description = "Test"
        val userIdWhoReport = "userIdWhoReport"
        var resourceSuccess = false
        whenever(userAuthRepositoryImpl.getCurrentUserId()).doReturn(userIdWhoReport)
        whenever(reportUserRepositoryImpl.createReport(any<ReportUser>())).doReturn(Unit)

        createReportUseCase(reportedUserId, description).collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { Assert.fail(result.error.message) }
                is Resource.Success -> { resourceSuccess = true }
            }
        }

        verify(userAuthRepositoryImpl).getCurrentUserId()
        verify(reportUserRepositoryImpl).createReport(any<ReportUser>())
        Assert.assertTrue(resourceSuccess)
    }

    @Test(expected = CancellationException::class)
    fun `create report first emit must be loading`() = runBlocking {
        val reportedUserId = "reportedUserId"
        val description = "Test"

        createReportUseCase(reportedUserId, description).collect { result ->
            when (result) {
                is Resource.Loading -> { this.cancel() }
                is Resource.Error -> { Assert.fail("Loading must be first") }
                is Resource.Success -> { Assert.fail("Loading must be first") }
            }
        }
    }
}