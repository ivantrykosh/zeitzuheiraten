package com.ivantrykosh.app.zeitzuheiraten.data.remote.github

import com.ivantrykosh.app.zeitzuheiraten.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class GitHubRepo {

    suspend fun getLatestAppVersion(): String? = withContext(Dispatchers.IO) {
        try {
            val url = URL(Constants.LATEST_APP_VERSION_NAME_URL)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            val responseCode = connection.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) return@withContext null

            val inputStream = connection.inputStream
            val response = inputStream.bufferedReader().use { it.readText() }
            val tagRegex = """"tag_name"\s*:\s*"([^"]+)"""".toRegex()
            val match = tagRegex.find(response)
            val version = match?.groupValues?.get(1)
            return@withContext version
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }
}