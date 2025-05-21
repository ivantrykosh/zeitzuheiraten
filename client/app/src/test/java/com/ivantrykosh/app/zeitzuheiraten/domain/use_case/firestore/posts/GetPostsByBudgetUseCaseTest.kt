package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.posts

import android.content.Context
import com.ivantrykosh.app.zeitzuheiraten.R
import com.ivantrykosh.app.zeitzuheiraten.data.repository.PostRepositoryImpl
import com.ivantrykosh.app.zeitzuheiraten.domain.model.PostWithRating
import com.ivantrykosh.app.zeitzuheiraten.utils.CategoryAndWeight
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
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class GetPostsByBudgetUseCaseTest {

    private lateinit var context: Context
    private lateinit var postRepositoryImpl: PostRepositoryImpl
    private lateinit var getPostsByBudgetUseCase: GetPostsByBudgetUseCase

    private val categoryMap = mapOf(
        R.string.photography_category to "Photography",
        R.string.videography_category to "Videography",
        R.string.music_category to "Music",
        R.string.officiants_category to "Officiants",
        R.string.venue_category to "Venus",
        R.string.catering_category to "Catering",
        R.string.transportation_category to "Transporting",
        R.string.beauty_category to "Hair & Makeup",
        R.string.event_rentals_category to "Event Rentals",
        R.string.entertainment_category to "Entertainment",
        R.string.wedding_planner_category to "Wedding planner",
        R.string.flowers_category to "Flowers",
        R.string.dresses_category to "Wedding dresses",
        R.string.fireworks_category to "Fireworks and specials effects",
        R.string.invitations_category to "Invitations",
        R.string.venue_decoration_category to "Venue decorations",
        R.string.cakes_category to "Cakes",
    )

    @Before
    fun setup() {
        context = mock {
            for ((resId, value) in categoryMap) {
                on { it.getString(resId) } doReturn value
            }
        }
        postRepositoryImpl = mock()
        getPostsByBudgetUseCase = GetPostsByBudgetUseCase(postRepositoryImpl, context)
    }

    @Test
    fun `get posts by budget successfully`() = runBlocking {
        val category = "Photography"
        val city = "Dnipro"
        val postsOrderType = PostsOrderType.BY_PRICE_DESC
        val startAfterLast = false
        val pageSize = 20
        var resourceSuccess = false
        val budget = 100000
        var actualPosts = listOf<PostWithRating>()
        val expectedPosts = listOf<PostWithRating>(PostWithRating())
        whenever(postRepositoryImpl.getPostsByFilters(eq(category), eq(city), any(), any(), eq(startAfterLast), eq(pageSize), eq(postsOrderType))).doReturn(expectedPosts)

        getPostsByBudgetUseCase.updateBudget(budget, listOf(CategoryAndWeight(category = "Photography", weight = 1)))
        getPostsByBudgetUseCase(category, city, startAfterLast, pageSize, postsOrderType).collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { Assert.fail(result.error.message) }
                is Resource.Success -> {
                    resourceSuccess = true
                    actualPosts = result.data!!
                }
            }
        }

        verify(postRepositoryImpl).getPostsByFilters(eq(category), eq(city), any(), any(), eq(startAfterLast), eq(pageSize), eq(postsOrderType))
        Assert.assertTrue(resourceSuccess)
        Assert.assertEquals(expectedPosts, actualPosts)
    }

    @Test(expected = CancellationException::class)
    fun `get posts by budget first emit must be loading`() = runBlocking {
        val category = "Photography"
        val city = "Dnipro"
        val postsOrderType = PostsOrderType.BY_PRICE_DESC
        val startAfterLast = false
        val pageSize = 20

        getPostsByBudgetUseCase(category, city, startAfterLast, pageSize, postsOrderType).collect { result ->
            when (result) {
                is Resource.Loading -> { this.cancel() }
                is Resource.Error -> { Assert.fail("Loading must be first") }
                is Resource.Success -> { Assert.fail("Loading must be first") }
            }
        }
    }
}