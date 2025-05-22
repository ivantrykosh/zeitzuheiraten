package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.chats

import android.util.Log
import com.ivantrykosh.app.zeitzuheiraten.domain.model.DisplayedChat
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.ChatRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserAuthRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

private const val LOG_TAG = "GetChatsForCurrentUserUseCase"

class GetChatsForCurrentUserUseCase @Inject constructor(
    private val userAuthRepository: UserAuthRepository,
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository,
) {
    /**
     * Get chats for current user and map them to [DisplayedChat]
     */
    operator fun invoke(startAfterLast: Boolean, pageSize: Int) = flow<Resource<List<DisplayedChat>>> {
        try {
            emit(Resource.Loading())
            val userId = userAuthRepository.getCurrentUserId()
            val chats = chatRepository.getChatsForUser(userId, startAfterLast, pageSize)
            val displayedChats = chats.map { chat ->
                val otherUserId = chat.users.first { it != userId }
                val username = userRepository.getUserById(otherUserId)?.name
                DisplayedChat(
                    id = chat.id,
                    withUserId = otherUserId,
                    withUsername = username
                )
            }
            emit(Resource.Success(displayedChats))
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message ?: "An error occurred")
            emit(Resource.Error(e))
        }
    }
}