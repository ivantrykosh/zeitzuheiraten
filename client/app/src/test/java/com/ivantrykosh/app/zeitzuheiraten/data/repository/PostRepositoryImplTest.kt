package com.ivantrykosh.app.zeitzuheiraten.data.repository

import com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.firestore.FirestorePosts
import com.ivantrykosh.app.zeitzuheiraten.domain.model.PostWithRating
import com.ivantrykosh.app.zeitzuheiraten.utils.PostsOrderType
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class PostRepositoryImplTest {

    private lateinit var mockFirestorePosts: FirestorePosts
    private lateinit var postRepositoryImpl: PostRepositoryImpl

    @Before
    fun setup() {
        mockFirestorePosts = mock()
        postRepositoryImpl = PostRepositoryImpl(mockFirestorePosts)
    }

    @Test
    fun `create post successfully`() = runTest{
        val post = PostWithRating()
        whenever(mockFirestorePosts.createPost(post)).doReturn(Unit)

        postRepositoryImpl.createPost(post)

        verify(mockFirestorePosts).createPost(post)
    }

    @Test
    fun `get posts by user id successfully`() = runTest{
        val userId = "userId"
        val expectedPosts = listOf(PostWithRating())
        whenever(mockFirestorePosts.getPostsByUserId(userId)).doReturn(expectedPosts)

        val actualPosts = postRepositoryImpl.getPostsByUserId(userId)

        verify(mockFirestorePosts).getPostsByUserId(userId)
        assertEquals(expectedPosts, actualPosts)
    }

    @Test
    fun `get post by id successfully`() = runTest{
        val postId = "postId"
        val expectedPost = PostWithRating(id = postId)
        whenever(mockFirestorePosts.getPostById(postId)).doReturn(expectedPost)

        val actualPost = postRepositoryImpl.getPostById(postId)

        verify(mockFirestorePosts).getPostById(postId)
        assertEquals(expectedPost, actualPost)
    }

    @Test
    fun `get posts by filters successfully`() = runTest{
        val category = "Video"
        val city = "Dnipro"
        val minPrice = null
        val maxPrice = 10000
        val startAfterLast = false
        val pageSize = 10
        val postsOrderType = PostsOrderType.BY_PRICE_DESC
        val expectedPosts = listOf(PostWithRating())
        whenever(mockFirestorePosts.getPostsByFilters(category, city, minPrice, maxPrice, startAfterLast, pageSize, postsOrderType)).doReturn(expectedPosts)

        val actualPosts = postRepositoryImpl.getPostsByFilters(category, city, minPrice, maxPrice, startAfterLast, pageSize, postsOrderType)

        verify(mockFirestorePosts).getPostsByFilters(category, city, minPrice, maxPrice, startAfterLast, pageSize, postsOrderType)
        assertEquals(expectedPosts, actualPosts)
    }

    @Test
    fun `update post successfully`() = runTest{
        val post = PostWithRating()
        whenever(mockFirestorePosts.updatePost(post)).doReturn(Unit)

        postRepositoryImpl.updatePost(post)

        verify(mockFirestorePosts).updatePost(post)
    }

    @Test
    fun `delete post successfully`() = runTest{
        val postId = "postId"
        whenever(mockFirestorePosts.deletePost(postId)).doReturn(Unit)

        postRepositoryImpl.deletePost(postId)

        verify(mockFirestorePosts).deletePost(postId)
    }
}