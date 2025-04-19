package com.ivantrykosh.app.zeitzuheiraten.presenter.main.shared.chats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivantrykosh.app.zeitzuheiraten.domain.model.DisplayedChat
import com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.chats.GetChatsForCurrentUserUseCase
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import com.ivantrykosh.app.zeitzuheiraten.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ChatsScreenViewModel @Inject constructor(
    private val getChatsForCurrentUserUseCase: GetChatsForCurrentUserUseCase,
) : ViewModel() {

    var getChatsState = MutableStateFlow(State<List<DisplayedChat>>())
        private set

    var lastChats = MutableStateFlow(emptyList<DisplayedChat>())
        private set

    var anyNewChats: Boolean = true
        private set

    private var pageSize = 10

    init {
        getChats()
    }

    fun getChats() {
        anyNewChats = true
        getChatsForCurrentUserUseCase(false, pageSize).onEach { result ->
            getChatsState.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> {
                    if (result.data!!.size < pageSize) {
                        anyNewChats = false
                    }
                    lastChats.value = result.data
                    State(data = lastChats.value)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getNewChats() {
        getChatsForCurrentUserUseCase(true, pageSize).onEach { result ->
            getChatsState.value = when (result) {
                is Resource.Error -> State(error = result.error)
                is Resource.Loading -> State(loading = true)
                is Resource.Success -> {
                    if (result.data!!.size < pageSize) {
                        anyNewChats = false
                    }
                    lastChats.value = lastChats.value.plus(result.data)
                    State(data = lastChats.value)
                }
            }
        }.launchIn(viewModelScope)
    }
}