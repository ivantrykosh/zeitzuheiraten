package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.messages

import com.ivantrykosh.app.zeitzuheiraten.domain.model.Message
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.ChatRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.MessageRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserAuthRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import java.time.Instant
import javax.inject.Inject

class CreateMessageUseCase @Inject constructor(
    private val userAuthRepository: UserAuthRepository,
    private val chatRepository: ChatRepository,
    private val messageRepository: MessageRepository,
) {
    /**
     * @param chatId if null then new chat will be created
     * @param otherUserId if chatId is null then otherUserId has not to be null
     */
    operator fun invoke(chatId: String?, message: String, otherUserId: String? = null) = flow<Resource<Unit>> {
        try {
            emit(Resource.Loading())
            val currentDateTime = Instant.now().toEpochMilli()
            val sender = userAuthRepository.getCurrentUserId()
            val message = Message(
                message = message,
                senderId = sender,
                dateTime = currentDateTime
            )
            val newChatId = chatId ?:
                chatRepository.createChat(sender, otherUserId!!, dateTime = currentDateTime)
            messageRepository.createMessage(newChatId, message)
            emit(Resource.Success())
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }
}