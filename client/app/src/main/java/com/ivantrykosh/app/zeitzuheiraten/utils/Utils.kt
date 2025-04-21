package com.ivantrykosh.app.zeitzuheiraten.utils

import android.content.Context
import androidx.annotation.StringRes
import com.ivantrykosh.app.zeitzuheiraten.R
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun Long.toStringDate(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd", Locale.getDefault())
    val date = LocalDateTime.ofInstant(Instant.ofEpochSecond(this / 1000), ZoneId.systemDefault())
    return date.format(formatter)
}

fun Long.toStringDateTime(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm", Locale.getDefault())
    val date = LocalDateTime.ofInstant(Instant.ofEpochSecond(this / 1000), ZoneId.systemDefault())
    return date.format(formatter)
}

enum class OrderType(
    @StringRes private val stringRes: Int
) {
    BY_CATEGORY(R.string.by_category),
    BY_RATING_DESC(R.string.by_rating),
    BY_PRICE_ASC(R.string.by_price_asc),
    BY_PRICE_DESC(R.string.by_price_desc);

    fun getString(context: Context): String {
        return context.getString(stringRes)
    }

    companion object {
        fun getObjectByString(context: Context, string: String): OrderType {
            return OrderType.entries.first {
                it.getString(context) == string
            }
        }

        fun toStringList(context: Context): List<String> {
            return OrderType.entries.map {
                it.getString(context)
            }
        }
    }
}