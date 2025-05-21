package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.chats

import com.ivantrykosh.app.zeitzuheiraten.data.repository.ChatRepositoryImpl
import com.ivantrykosh.app.zeitzuheiraten.data.repository.UserAuthRepositoryImpl
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
class GetChatIdByUsersUseCaseTest {

    private lateinit var userAuthRepositoryImpl: UserAuthRepositoryImpl
    private lateinit var chatRepositoryImpl: ChatRepositoryImpl
    private lateinit var getChatIdByUsersUseCase: GetChatIdByUsersUseCase

    @Before
    fun setup() {
        userAuthRepositoryImpl = mock()
        chatRepositoryImpl = mock()
        getChatIdByUsersUseCase = GetChatIdByUsersUseCase(userAuthRepositoryImpl, chatRepositoryImpl)
    }

    @Test
    fun `get chat id by users successfully`() = runBlocking {
        val user1Id = "user1Id"
        val user2Id = "user2Id"
        val expectedChatId = "chatId"
        var actualChatId = ""
        whenever(userAuthRepositoryImpl.getCurrentUserId()).doReturn(user1Id)
        whenever(chatRepositoryImpl.getChatByUsers(user1Id, user2Id)).doReturn(expectedChatId)

        getChatIdByUsersUseCase(user2Id).collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { Assert.fail(result.error.message) }
                is Resource.Success -> { actualChatId = result.data!! }
            }
        }

        verify(userAuthRepositoryImpl).getCurrentUserId()
        verify(chatRepositoryImpl).getChatByUsers(user1Id, user2Id)
        Assert.assertEquals(expectedChatId, actualChatId)
    }

    @Test(expected = CancellationException::class)
    fun `get chat id by users first emit must be loading`() = runBlocking {
        val user2Id = "user2Id"

        getChatIdByUsersUseCase(user2Id).collect { result ->
            when (result) {
                is Resource.Loading -> { this.cancel() }
                is Resource.Error -> { Assert.fail("Loading must be first") }
                is Resource.Success -> { Assert.fail("Loading must be first") }
            }
        }
    }
}