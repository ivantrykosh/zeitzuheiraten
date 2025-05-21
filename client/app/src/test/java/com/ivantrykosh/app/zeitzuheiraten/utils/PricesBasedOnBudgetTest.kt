package com.ivantrykosh.app.zeitzuheiraten.utils

import android.content.Context
import com.ivantrykosh.app.zeitzuheiraten.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class PricesBasedOnBudgetTest {

    private lateinit var context: Context

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
        context = mock<Context> {
            for ((resId, value) in categoryMap) {
                on { it.getString(resId) } doReturn value
            }
        }
    }

    @Test
    fun `test with 0 budget`() {
        val categories = listOf(
            CategoryAndWeight("Photography", 3),
            CategoryAndWeight("Cakes", 5),
        )
        val budget = 0
        val prices = PricesBasedOnBudget(context, budget, categories).optimalPrices
        assertEquals(categories.size, prices.size)
        assertEquals(0, prices.values.sum())
    }

    @Test
    fun `test optimalPrices with sample data`() {
        val categories = listOf(
            CategoryAndWeight("Photography", 5),
            CategoryAndWeight("Videography", 4),
            CategoryAndWeight("Venus", 3)
        )
        val budget = 10000

        val prices = PricesBasedOnBudget(context, budget, categories).optimalPrices

        assertTrue(prices.containsKey("Photography"))
        assertTrue(prices.containsKey("Videography"))
        assertTrue(prices.containsKey("Venus"))

        val expectedPhotography = ((5f/12*0.4f + 7f/33*0.6f) * budget).toInt()
        val expectedVideography = ((4f/12*0.4f + 6f/33*0.6f) * budget).toInt()
        val expectedVenue = ((3f/12*0.4f + 20f/33*0.6f) * budget).toInt()

        assertEquals(expectedPhotography, prices["Photography"])
        assertEquals(expectedVideography, prices["Videography"])
        assertEquals(expectedVenue, prices["Venus"])
    }
}