package com.ivantrykosh.app.zeitzuheiraten.presenter.main.shared.chats.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivantrykosh.app.zeitzuheiraten.domain.model.ChatMessage
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.auth.GetCurrentUserIdUseCase
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.chats.GetChatIdByUsersUseCase
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.messages.CreateMessageUseCase
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.messages.GetMessagesForChatUseCase
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.messages.ObserveMessagesUseCase
import com.ivantrykosh.app.zeitzuheiraten.presenter.clearState
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import com.ivantrykosh.app.zeitzuheiraten.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.time.Instant
import javax.inject.Inject

private const val LOG_TAG = "MessagesViewModel"

@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val getChatIdByUsersUseCase: GetChatIdByUsersUseCase,
    private val getMessagesForChatUseCase: GetMessagesForChatUseCase,
    private val observeMessagesUseCase: ObserveMessagesUseCase,
    private val createMessageUseCase: CreateMessageUseCase,
) : ViewModel() {

    var getCurrentUserIdState = MutableStateFlow(State<String>())
        private set

    var getChatByUsersState = MutableStateFlow(State<String?>())
        private set

    var getMessagesState = MutableStateFlow(State<List<ChatMessage>>())
        private set

    var createMessageState = MutableStateFlow(State<Unit>())
        private set

    var lastMessages = MutableStateFlow(emptyList<ChatMessage>())
        private set

    var anyNewMessages: Boolean = true
        private set

    private var chatId: String? = null

    private var pageSize = 20

    fun setChatId(chatId: String) {
        this.chatId = chatId
    }

    init {
        getCurrentUserId()
    }

    fun clearCreateMessageState() = clearState(createMessageState)

    fun clearGetChatByUsersState() = clearState(getChatByUsersState)

    fun clearGetMessagesState() = clearState(getMessagesState)

    private fun getCurrentUserId() {
        getCurrentUserIdUseCase().onEach { result ->
            getCurrentUserIdState.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> State(data = result.data)
            }
        }.launchIn(viewModelScope)
    }

    fun getChatIdByUsers(user2Id: String) {
        getChatIdByUsersUseCase(user2Id).onEach { result ->
            getChatByUsersState.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> {
                    chatId = result.data
                    State(data = result.data)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getMessages() {
        anyNewMessages = true
        getMessagesForChatUseCase(chatId!!, false, pageSize).onEach { result ->
            getMessagesState.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> {
                    if (result.data!!.size < pageSize) {
                        anyNewMessages = false
                    }
                    lastMessages.value = result.data.map {
                        ChatMessage(
                            id = it.id,
                            message = it.message,
                            dateTime = it.dateTime,
                            isMyMessage = it.senderId == getCurrentUserIdState.value.data!!
                        )
                    }
                    observeMessages()
                    State(data = lastMessages.value)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getNewMessages() {
        getMessagesForChatUseCase(chatId!!, true, pageSize).onEach { result ->
            getMessagesState.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> {
                    if (result.data!!.size < pageSize) {
                        anyNewMessages = false
                    }
                    lastMessages.value = lastMessages.value.plus(
                        result.data.map {
                            ChatMessage(
                                id = it.id,
                                message = it.message,
                                dateTime = it.dateTime,
                                isMyMessage = it.senderId == getCurrentUserIdState.value.data!!
                            )
                        })
                    State(data = lastMessages.value)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun observeMessages() {
        val lastDateTime = lastMessages.value.firstOrNull()?.dateTime ?: Instant.now().toEpochMilli()
        observeMessagesUseCase(chatId!!, lastDateTime).onEach { result ->
            if (result.isNotEmpty()) {
                var newMessages = result
                    .map {
                        ChatMessage(
                            id = it.id,
                            message = it.message,
                            dateTime = it.dateTime,
                            isMyMessage = it.senderId == getCurrentUserIdState.value.data!!
                        )
                    }.filter { message ->
                        lastMessages.value.none { it.id == message.id }
                    }
                newMessages = newMessages.plus(lastMessages.value)
                lastMessages.value = newMessages
            }
        }
            .catch {
                Log.e(LOG_TAG, it.message ?: "An error occurred")
            }
            .launchIn(viewModelScope)
    }

    fun createMessage(message: String, otherUserId: String?) {
        createMessageUseCase(message = message, otherUserId = otherUserId, chatId = chatId).onEach { result ->
            createMessageState.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> State(data = Unit)
            }
        }.launchIn(viewModelScope)
    }
}