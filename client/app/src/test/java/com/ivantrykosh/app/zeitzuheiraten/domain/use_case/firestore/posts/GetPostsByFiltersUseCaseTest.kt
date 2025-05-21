package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.posts

import com.ivantrykosh.app.zeitzuheiraten.data.repository.PostRepositoryImpl
import com.ivantrykosh.app.zeitzuheiraten.domain.model.PostWithRating
import com.ivantrykosh.app.zeitzuheiraten.utils.PostsOrderType
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
class GetPostsByFiltersUseCaseTest {

    private lateinit var postRepositoryImpl: PostRepositoryImpl
    private lateinit var getPostsByFiltersUseCase: GetPostsByFiltersUseCase


    @Before
    fun setup() {
        postRepositoryImpl = mock()
        getPostsByFiltersUseCase = GetPostsByFiltersUseCase(postRepositoryImpl)
    }

    @Test
    fun `get posts by filters successfully`() = runBlocking {
        val category = "Photography"
        val city = "Dnipro"
        val maxPrice = 10000
        val postsOrderType = PostsOrderType.BY_PRICE_DESC
        val startAfterLast = false
        val pageSize = 20
        var resourceSuccess = false
        var actualPosts = listOf<PostWithRating>()
        val expectedPosts = listOf<PostWithRating>(PostWithRating())
        whenever(postRepositoryImpl.getPostsByFilters(category, city, null, maxPrice, startAfterLast, pageSize, postsOrderType)).doReturn(expectedPosts)

        getPostsByFiltersUseCase(category, city, maxPrice, startAfterLast, pageSize, postsOrderType).collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { Assert.fail(result.error.message) }
                is Resource.Success -> {
                    resourceSuccess = true
                    actualPosts = result.data!!
                }
            }
        }

        verify(postRepositoryImpl).getPostsByFilters(category, city, null, maxPrice, startAfterLast, pageSize, postsOrderType)
        Assert.assertTrue(resourceSuccess)
        Assert.assertEquals(expectedPosts, actualPosts)
    }

    @Test(expected = CancellationException::class)
    fun `get posts by filters first emit must be loading`() = runBlocking {
        val category = "Photography"
        val city = "Dnipro"
        val maxPrice = 10000
        val postsOrderType = PostsOrderType.BY_PRICE_DESC
        val startAfterLast = false
        val pageSize = 20

        getPostsByFiltersUseCase(category, city, maxPrice, startAfterLast, pageSize, postsOrderType).collect { result ->
            when (result) {
                is Resource.Loading -> { this.cancel() }
                is Resource.Error -> { Assert.fail("Loading must be first") }
                is Resource.Success -> { Assert.fail("Loading must be first") }
            }
        }
    }
}