package com.ivantrykosh.app.zeitzuheiraten.presenter.main.shared.chats

import androidx.lifecycle.ViewModel
import com.ivantrykosh.app.zeitzuheiraten.domain.model.DisplayedChat
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.chats.GetChatsForCurrentUserUseCase
import com.ivantrykosh.app.zeitzuheiraten.presenter.loadPaginatedData
import com.ivantrykosh.app.zeitzuheiraten.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class ChatsScreenViewModel @Inject constructor(
    private val getChatsForCurrentUserUseCase: GetChatsForCurrentUserUseCase,
) : ViewModel() {

    var getChatsState = MutableStateFlow(State<Unit>())
        private set

    var lastChats = MutableStateFlow(emptyList<DisplayedChat>())
        private set

    var anyNewChats: Boolean = true
        private set

    private var pageSize = 10

    init {
        getChats(reset = true)
    }

    fun getChats(reset: Boolean) {
        loadPaginatedData(
            reset = reset,
            pageSize = pageSize,
            anyNewItems = { anyNewChats = it },
            stateFlow = getChatsState,
            resultFlow = lastChats,
            useCaseCall = { size -> getChatsForCurrentUserUseCase(!reset, size) }
        )
    }
}