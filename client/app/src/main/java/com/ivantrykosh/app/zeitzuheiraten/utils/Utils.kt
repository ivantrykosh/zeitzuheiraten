package com.ivantrykosh.app.zeitzuheiraten.utils

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Convert long to string with format yyyy/MM/dd (e.g. 2020/04/26).
 * It uses default locale and time zone.
 * Long value has to be in millis from the Epoch.
 */
fun Long.toStringDate(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd", Locale.getDefault())
    val date = LocalDateTime.ofInstant(Instant.ofEpochSecond(this / 1000), ZoneId.systemDefault())
    return date.format(formatter)
}


/**
 * Convert long to string with format yyyy/MM/dd HH:mm (e.g. 2020/04/26 14:46).
 * It uses default locale and time zone.
 * Long value has to be in millis from the Epoch.
 */
fun Long.toStringDateTime(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm", Locale.getDefault())
    val date = LocalDateTime.ofInstant(Instant.ofEpochSecond(this / 1000), ZoneId.systemDefault())
    return date.format(formatter)
}