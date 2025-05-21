package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.posts

import com.ivantrykosh.app.zeitzuheiraten.data.repository.PostRepositoryImpl
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
class DeletePostByIdUseCaseTest {

    private lateinit var postRepositoryImpl: PostRepositoryImpl
    private lateinit var deletePostByIdUseCase: DeletePostByIdUseCase

    @Before
    fun setup() {
        postRepositoryImpl = mock()
        deletePostByIdUseCase = DeletePostByIdUseCase(postRepositoryImpl)
    }

    @Test
    fun `delete post by id successfully`() = runBlocking {
        val postId = "postId"
        var resourceSuccess = false
        whenever(postRepositoryImpl.deletePost(postId)).doReturn(Unit)

        deletePostByIdUseCase(postId).collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { Assert.fail(result.error.message) }
                is Resource.Success -> { resourceSuccess = true }
            }
        }

        verify(postRepositoryImpl).deletePost(postId)
        Assert.assertTrue(resourceSuccess)
    }

    @Test(expected = CancellationException::class)
    fun `delete post by id first emit must be loading`() = runBlocking {
        val postId = "postId"

        deletePostByIdUseCase(postId).collect { result ->
            when (result) {
                is Resource.Loading -> { this.cancel() }
                is Resource.Error -> { Assert.fail("Loading must be first") }
                is Resource.Success -> { Assert.fail("Loading must be first") }
            }
        }
    }
}