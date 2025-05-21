package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.posts

import com.ivantrykosh.app.zeitzuheiraten.data.repository.PostRepositoryImpl
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
class GetPostByIdUseCaseTest {

    private lateinit var postRepositoryImpl: PostRepositoryImpl
    private lateinit var getPostByIdUseCase: GetPostByIdUseCase

    @Before
    fun setup() {
        postRepositoryImpl = mock()
        getPostByIdUseCase = GetPostByIdUseCase(postRepositoryImpl)
    }

    @Test
    fun `get post by id successfully`() = runBlocking {
        val postId = "postId"
        val expectedPost = PostWithRating()
        var actualPost: PostWithRating? = null
        var resourceSuccess = false
        whenever(postRepositoryImpl.getPostById(postId)).doReturn(expectedPost)

        getPostByIdUseCase(postId).collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { Assert.fail(result.error.message) }
                is Resource.Success -> {
                    resourceSuccess = true
                    actualPost = result.data
                }
            }
        }

        verify(postRepositoryImpl).getPostById(postId)
        Assert.assertTrue(resourceSuccess)
        Assert.assertEquals(expectedPost, actualPost)
    }

    @Test(expected = CancellationException::class)
    fun `get post by id first emit must be loading`() = runBlocking {
        val postId = "postId"

        getPostByIdUseCase(postId).collect { result ->
            when (result) {
                is Resource.Loading -> { this.cancel() }
                is Resource.Error -> { Assert.fail("Loading must be first") }
                is Resource.Success -> { Assert.fail("Loading must be first") }
            }
        }
    }
}