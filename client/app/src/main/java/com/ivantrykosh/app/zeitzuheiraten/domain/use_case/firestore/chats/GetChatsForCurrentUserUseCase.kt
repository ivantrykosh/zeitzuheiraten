package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.chats

import com.ivantrykosh.app.zeitzuheiraten.domain.model.DisplayedChat
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.ChatRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserAuthRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetChatsForCurrentUserUseCase @Inject constructor(
    private val userAuthRepository: UserAuthRepository,
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository,
) {
    operator fun invoke(startAfterLast: Boolean, pageSize: Int) = flow<Resource<List<DisplayedChat>>> {
        try {
            emit(Resource.Loading())
            val userId = userAuthRepository.getCurrentUserId()
            val chats = chatRepository.getChatsForUser(userId, startAfterLast, pageSize)
            val displayedChats = chats.map { chat ->
                val otherUserId = chat.users.first { it != userId }
                val username = userRepository.getUserById(otherUserId).name
                DisplayedChat(
                    id = chat.id,
                    withUserId = otherUserId,
                    withUsername = username
                )
            }
            emit(Resource.Success(displayedChats))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }
}