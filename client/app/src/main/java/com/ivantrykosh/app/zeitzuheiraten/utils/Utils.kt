package com.ivantrykosh.app.zeitzuheiraten.utils

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