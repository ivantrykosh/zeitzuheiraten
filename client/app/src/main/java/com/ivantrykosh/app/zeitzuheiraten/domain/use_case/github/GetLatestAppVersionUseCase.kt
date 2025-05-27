package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.github

import android.util.Log
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.GitHubRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

private const val LOG_TAG = "GetLatestAppVersionUseCase"

class GetLatestAppVersionUseCase @Inject constructor(
    private val gitHubRepository: GitHubRepository
) {
    operator fun invoke() = flow<Resource<String>> {
        try {
            emit(Resource.Loading())
            val latestVersion = gitHubRepository.getLatestAppVersion()
            emit(Resource.Success(latestVersion))
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message ?: "An error occurred")
            emit(Resource.Error(e))
        }
    }
}