package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.chats

import com.ivantrykosh.app.zeitzuheiraten.data.repository.ChatRepositoryImpl
import com.ivantrykosh.app.zeitzuheiraten.data.repository.UserAuthRepositoryImpl
import com.ivantrykosh.app.zeitzuheiraten.data.repository.UserRepositoryImpl
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Chat
import com.ivantrykosh.app.zeitzuheiraten.domain.model.DisplayedChat
import com.ivantrykosh.app.zeitzuheiraten.domain.model.User
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class GetChatsForCurrentUserUseCaseTest {

    private lateinit var userAuthRepositoryImpl: UserAuthRepositoryImpl
    private lateinit var userRepositoryImpl: UserRepositoryImpl
    private lateinit var chatRepositoryImpl: ChatRepositoryImpl
    private lateinit var getChatsForCurrentUserUseCase: GetChatsForCurrentUserUseCase

    @Before
    fun setup() {
        userAuthRepositoryImpl = mock()
        userRepositoryImpl = mock()
        chatRepositoryImpl = mock()
        getChatsForCurrentUserUseCase = GetChatsForCurrentUserUseCase(userAuthRepositoryImpl, userRepositoryImpl, chatRepositoryImpl)
    }

    @Test
    fun `get chats for current user successfully`() = runBlocking {
        val startAfterLast = false
        val pageSize = 20
        val user1Id = "user1Id"
        val user2 = User(id = "user2Id", name = "User2")
        val chats = listOf(Chat(id = "chatId", users = listOf(user1Id, user2.id)))
        var resourceSuccess = false
        var actualChats = listOf<DisplayedChat>()
        val expectedChats = listOf<DisplayedChat>(DisplayedChat(id = "chatId", withUserId = user2.id, withUsername = user2.name))
        whenever(userAuthRepositoryImpl.getCurrentUserId()).doReturn(user1Id)
        whenever(chatRepositoryImpl.getChatsForUser(user1Id, startAfterLast, pageSize)).doReturn(chats)
        whenever(userRepositoryImpl.getUserById(user2.id)).doReturn(user2)

        getChatsForCurrentUserUseCase(startAfterLast, pageSize).collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { Assert.fail(result.error.message) }
                is Resource.Success -> {
                    resourceSuccess = true
                    actualChats = result.data!!
                }
            }
        }

        verify(userAuthRepositoryImpl).getCurrentUserId()
        verify(chatRepositoryImpl).getChatsForUser(user1Id, startAfterLast, pageSize)
        verify(userRepositoryImpl).getUserById(user2.id)
        Assert.assertTrue(resourceSuccess)
        Assert.assertEquals(expectedChats, actualChats)
    }

    @Test(expected = CancellationException::class)
    fun `get chats for current user first emit must be loading`() = runBlocking {
        val startAfterLast = false
        val pageSize = 20

        getChatsForCurrentUserUseCase(startAfterLast, pageSize).collect { result ->
            when (result) {
                is Resource.Loading -> { this.cancel() }
                is Resource.Error -> { Assert.fail("Loading must be first") }
                is Resource.Success -> { Assert.fail("Loading must be first") }
            }
        }
    }
}