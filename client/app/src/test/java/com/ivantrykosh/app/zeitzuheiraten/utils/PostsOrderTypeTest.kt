package com.ivantrykosh.app.zeitzuheiraten.utils

import android.content.Context
import com.ivantrykosh.app.zeitzuheiraten.R
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class PostsOrderTypeTest {

    private lateinit var context: Context
    private val orders = mapOf(
        R.string.by_category to "By category",
        R.string.by_rating to "By rating",
        R.string.by_number_of_feedbacks to "By number of feedbacks",
        R.string.by_price_asc to "By price ascending",
        R.string.by_price_desc to "By price descending",
    )

    @Before
    fun setup() {
        context = mock<Context> {
            for ((resId, value) in orders) {
                on { it.getString(resId) } doReturn value
            }
        }
    }

    @Test
    fun `get string test`() {
        assertEquals("By category", PostsOrderType.BY_CATEGORY.getString(context))
        assertEquals("By rating", PostsOrderType.BY_RATING_DESC.getString(context))
        assertEquals("By number of feedbacks", PostsOrderType.BY_NUMBER_OF_FEEDBACKS.getString(context))
        assertEquals("By price ascending", PostsOrderType.BY_PRICE_ASC.getString(context))
        assertEquals("By price descending", PostsOrderType.BY_PRICE_DESC.getString(context))
    }

    @Test
    fun `get object by string test`() {
        assertEquals(PostsOrderType.BY_CATEGORY, PostsOrderType.getObjectByString(context, "By category"))
        assertEquals(PostsOrderType.BY_RATING_DESC, PostsOrderType.getObjectByString(context, "By rating"))
        assertEquals(PostsOrderType.BY_NUMBER_OF_FEEDBACKS, PostsOrderType.getObjectByString(context, "By number of feedbacks"))
        assertEquals(PostsOrderType.BY_PRICE_ASC, PostsOrderType.getObjectByString(context, "By price ascending"))
        assertEquals(PostsOrderType.BY_PRICE_DESC, PostsOrderType.getObjectByString(context, "By price descending"))
    }

    @Test
    fun `objects to string list test`() {
        val strings = PostsOrderType.toStringList(context)

        assertEquals("By category", strings[0])
        assertEquals("By rating", strings[1])
        assertEquals("By number of feedbacks", strings[2])
        assertEquals("By price ascending", strings[3])
        assertEquals("By price descending", strings[4])
    }
}