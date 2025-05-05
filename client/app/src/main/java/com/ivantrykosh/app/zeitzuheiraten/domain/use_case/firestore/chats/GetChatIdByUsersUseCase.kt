package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.chats

import android.util.Log
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.ChatRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserAuthRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

private const val LOG_TAG = "GetChatIdByUsersUseCase"

class GetChatIdByUsersUseCase @Inject constructor(
    private val userAuthRepository: UserAuthRepository,
    private val chatRepository: ChatRepository,
) {
    operator fun invoke(user2Id: String) = flow<Resource<String?>> {
        try {
            emit(Resource.Loading())
            val user1Id = userAuthRepository.getCurrentUserId()
            val chatId = chatRepository.getChatByUsers(user1Id, user2Id)
            emit(Resource.Success(chatId))
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message ?: "An error occurred")
            emit(Resource.Error(e))
        }
    }
}