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

enum class PostsOrderType(
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

enum class BookingsFilterType(
    @StringRes private val stringRes: Int
) {
    NOT_CONFIRMED(R.string.not_confirmed),
    CONFIRMED(R.string.confirmed),
    CANCELED(R.string.canceled),
    SERVICE_PROVIDED(R.string.service_provided);

    fun getString(context: Context): String {
        return context.getString(stringRes)
    }

    companion object {
        fun getObjectByString(context: Context, string: String): BookingsFilterType {
            return BookingsFilterType.entries.first {
                it.getString(context) == string
            }
        }

        fun toStringList(context: Context): List<String> {
            return BookingsFilterType.entries.map {
                it.getString(context)
            }
        }
    }
}

enum class BookingStatus {
    NOT_CONFIRMED, CONFIRMED, CANCELED, SERVICE_PROVIDED;

    companion object {
        fun stringToValue(name: String): BookingStatus {
            for (entry in entries) {
                if (entry.name == name) {
                    return entry
                }
            }
            throw IllegalArgumentException("Booking status with name $name does not exist")
        }
    }
}

fun Context.getAppLocale(): Locale {
    return this.resources.configuration.locales[0]
}