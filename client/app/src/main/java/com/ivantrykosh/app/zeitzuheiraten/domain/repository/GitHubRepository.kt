package com.ivantrykosh.app.zeitzuheiraten.domain.repository

interface GitHubRepository {

    suspend fun getLatestAppVersion(): String?
}