package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.chats

import com.ivantrykosh.app.zeitzuheiraten.domain.repository.ChatRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserAuthRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

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
            emit(Resource.Error(e))
        }
    }
}