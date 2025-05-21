package com.ivantrykosh.app.zeitzuheiraten.utils

import android.content.Context
import androidx.annotation.StringRes
import com.ivantrykosh.app.zeitzuheiraten.R

enum class PostsOrderType(
    @StringRes private val stringRes: Int
) {
    BY_CATEGORY(R.string.by_category),
    BY_RATING_DESC(R.string.by_rating),
    BY_NUMBER_OF_FEEDBACKS(R.string.by_number_of_feedbacks),
    BY_PRICE_ASC(R.string.by_price_asc),
    BY_PRICE_DESC(R.string.by_price_desc);

    fun getString(context: Context): String {
        return context.getString(stringRes)
    }

    companion object {
        fun getObjectByString(context: Context, string: String): PostsOrderType {
            return PostsOrderType.entries.first {
                it.getString(context) == string
            }
        }

        fun toStringList(context: Context): List<String> {
            return PostsOrderType.entries.map {
                it.getString(context)
            }
        }
    }
}