package com.ivantrykosh.app.zeitzuheiraten.utils

object Constants {
    const val VALID_EMAIL_REGEX_PATTERN = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,10}$"
    const val VALID_PASSWORD_REGEX_PATTERN = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,64}"
    const val USER_PROFILE_PICTURE_NAME = "avatar"
    const val MAX_FILE_SIZE = 5 * 1024 * 1024 // 5 MB
}