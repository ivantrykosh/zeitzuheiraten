package com.ivantrykosh.app.zeitzuheiraten.utils

import android.content.Context
import com.ivantrykosh.app.zeitzuheiraten.R

data class CategoryAndWeight(
    val category: String,
    val weight: Int,
)

class PricesBasedOnBudget(
    context: Context,
    private val budget: Int,
    private val categories: List<CategoryAndWeight>,
) {
    private val defaultWeights =
        mapOf(
            context.getString(R.string.photography_category) to 7,
            context.getString(R.string.videography_category) to 6,
            context.getString(R.string.music_category) to 5,
            context.getString(R.string.officiants_category) to 5,
            context.getString(R.string.venue_category) to 20,
            context.getString(R.string.catering_category) to 20,
            context.getString(R.string.transportation_category) to 2,
            context.getString(R.string.beauty_category) to 3,
            context.getString(R.string.event_rentals_category) to 3,
            context.getString(R.string.entertainment_category) to 2,
            context.getString(R.string.wedding_planner_category) to 6,
            context.getString(R.string.flowers_category) to 3,
            context.getString(R.string.dresses_category) to 7,
            context.getString(R.string.fireworks_category) to 1,
            context.getString(R.string.invitations_category) to 3,
            context.getString(R.string.venue_decoration_category) to 4,
            context.getString(R.string.cakes_category) to 3,
        )

    val optimalPrices by lazy {
        getOptimalPriceForAllCategories()
    }

    private fun getOptimalPriceForAllCategories(): Map<String, Int> {
        val sumOfWeights = calculateSumOfWeights()
        val sumOfDefaultWeights = calculateSumOfDefaultWeights()
        return categories.associate { entry ->
            entry.category to calculatePriceForCategory(entry.category, sumOfWeights, sumOfDefaultWeights)
        }
    }

    private fun calculatePriceForCategory(category: String, sumOfWeights: Int, sumOfDefaultWeights: Int): Int {
        val weight = categories.first { it.category == category }.weight
        val defaultWeight = defaultWeights[category]!!
        val coefficient = weight / sumOfWeights.toFloat()
        val defaultCoefficient = defaultWeight / sumOfDefaultWeights.toFloat()
        val finalCoefficient = coefficient * 0.4 + defaultCoefficient * 0.6
        val finalPrice = finalCoefficient * budget
        return finalPrice.toInt()
    }

    private fun calculateSumOfDefaultWeights(): Int {
        return defaultWeights
            .filterKeys { key -> categories.any { it.category == key } }
            .entries.sumOf { entry ->
                entry.value
            }
    }

    private fun calculateSumOfWeights(): Int {
        return categories
            .sumOf { entry ->
                entry.weight
            }
    }
}