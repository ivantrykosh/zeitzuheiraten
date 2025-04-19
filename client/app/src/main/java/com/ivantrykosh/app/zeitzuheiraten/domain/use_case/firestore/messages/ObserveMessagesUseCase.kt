package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.messages

import com.ivantrykosh.app.zeitzuheiraten.domain.repository.MessageRepository
import javax.inject.Inject

class ObserveMessagesUseCase @Inject constructor(
    private val messageRepository: MessageRepository,
) {
    operator fun invoke(chatId: String, afterDateTime: Long) =
        messageRepository.observeMessages(chatId, afterDateTime)
}