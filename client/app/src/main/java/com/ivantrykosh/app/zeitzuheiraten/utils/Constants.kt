package com.ivantrykosh.app.zeitzuheiraten.utils

object Constants {
    const val VALID_EMAIL_REGEX_PATTERN = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,10}$"
    const val VALID_PASSWORD_REGEX_PATTERN = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,64}"
    const val USER_PROFILE_PICTURE_NAME = "avatar"
    const val MAX_FILE_SIZE = 5 * 1024 * 1024 // 5 MB
    const val MAX_POSTS_PER_USER = 20
    const val MAX_CITIES_PER_POST = 5
    const val MAX_SYMBOLS_FOR_POST_DESCRIPTION = 500
    const val MAX_NOT_AVAILABLE_DATE_RANGES_PER_POST = 5
    const val MAX_IMAGES_PER_POST = 10
    const val MAX_SYMBOLS_FOR_FEEDBACK_DESCRIPTION = 200
    const val MAX_SYMBOLS_FOR_MESSAGE = 1000
    const val MAX_SYMBOLS_FOR_REPORT_DESCRIPTION = 1000
}