package com.ivantrykosh.app.zeitzuheiraten.data.repository

import com.ivantrykosh.app.zeitzuheiraten.data.remote.github.GitHubRepo
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.GitHubRepository
import javax.inject.Inject

class GitHubRepositoryImpl @Inject constructor(
    private val gitHub: GitHubRepo
) : GitHubRepository {
    override suspend fun getLatestAppVersion(): String? {
        return gitHub.getLatestAppVersion()
    }
}