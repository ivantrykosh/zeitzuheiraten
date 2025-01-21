package com.ivantrykosh.app.zeitzuheiraten.utils

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns

fun isEmailValid(email: String): Boolean {
    val regex = Regex(Constants.VALID_EMAIL_REGEX_PATTERN, RegexOption.IGNORE_CASE)
    return email.matches(regex)
}

fun isPasswordValid(password: String): Boolean {
    val regex = Regex(Constants.VALID_PASSWORD_REGEX_PATTERN)
    return password.matches(regex)
}

fun isNameValid(name: String): Boolean {
    return name.isNotBlank()
}

fun isFileSizeAppropriate(uri: Uri, contentResolver: ContentResolver): Boolean {
    val fileSize = contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
        cursor.moveToFirst()
        if (sizeIndex != -1) {
            cursor.getLong(sizeIndex)
        } else {
            0
        }
    } ?: 0
    return fileSize <= Constants.MAX_FILE_SIZE
}