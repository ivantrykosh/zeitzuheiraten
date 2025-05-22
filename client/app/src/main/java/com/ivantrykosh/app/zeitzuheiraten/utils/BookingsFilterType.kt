package com.ivantrykosh.app.zeitzuheiraten.utils

import android.content.Context
import androidx.annotation.StringRes
import com.ivantrykosh.app.zeitzuheiraten.R

/**
 * Booking filters and their corresponding string resources
 */
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