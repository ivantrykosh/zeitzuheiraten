package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.messages

import com.ivantrykosh.app.zeitzuheiraten.domain.model.Message
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.MessageRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetMessagesForChatUseCase @Inject constructor(
    private val messageRepository: MessageRepository,
) {
    operator fun invoke(chatId: String, startAfterLast: Boolean, pageSize: Int) = flow<Resource<List<Message>>> {
        try {
            emit(Resource.Loading())
            val messages = messageRepository.getMessages(chatId, startAfterLast, pageSize)
            emit(Resource.Success(messages))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }
}