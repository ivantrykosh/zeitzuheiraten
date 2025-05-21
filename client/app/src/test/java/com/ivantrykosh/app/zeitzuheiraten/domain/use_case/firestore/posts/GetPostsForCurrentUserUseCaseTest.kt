package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.posts

import com.ivantrykosh.app.zeitzuheiraten.data.repository.PostRepositoryImpl
import com.ivantrykosh.app.zeitzuheiraten.data.repository.UserAuthRepositoryImpl
import com.ivantrykosh.app.zeitzuheiraten.domain.model.PostWithRating
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
class GetPostsForCurrentUserUseCaseTest {

    private lateinit var userAuthRepositoryImpl: UserAuthRepositoryImpl
    private lateinit var postRepositoryImpl: PostRepositoryImpl
    private lateinit var getPostsForCurrentUserUseCase: GetPostsForCurrentUserUseCase


    @Before
    fun setup() {
        userAuthRepositoryImpl = mock()
        postRepositoryImpl = mock()
        getPostsForCurrentUserUseCase = GetPostsForCurrentUserUseCase(userAuthRepositoryImpl, postRepositoryImpl)
    }

    @Test
    fun `get posts for current user successfully`() = runBlocking {
        val userId = "userId"
        var resourceSuccess = false
        var actualPosts = listOf<PostWithRating>()
        val expectedPosts = listOf<PostWithRating>(PostWithRating())
        whenever(userAuthRepositoryImpl.getCurrentUserId()).doReturn(userId)
        whenever(postRepositoryImpl.getPostsByUserId(userId)).doReturn(expectedPosts)

        getPostsForCurrentUserUseCase().collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { Assert.fail(result.error.message) }
                is Resource.Success -> {
                    resourceSuccess = true
                    actualPosts = result.data!!
                }
            }
        }

        verify(userAuthRepositoryImpl).getCurrentUserId()
        verify(postRepositoryImpl).getPostsByUserId(userId)
        Assert.assertTrue(resourceSuccess)
        Assert.assertEquals(expectedPosts, actualPosts)
    }

    @Test(expected = CancellationException::class)
    fun `get posts for current user first emit must be loading`() = runBlocking {
        getPostsForCurrentUserUseCase().collect { result ->
            when (result) {
                is Resource.Loading -> { this.cancel() }
                is Resource.Error -> { Assert.fail("Loading must be first") }
                is Resource.Success -> { Assert.fail("Loading must be first") }
            }
        }
    }
}