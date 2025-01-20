package com.ivantrykosh.app.zeitzuheiraten.utils

fun isEmailValid(email: String): Boolean {
    val regex = Regex(Constants.VALID_EMAIL_REGEX_PATTERN, RegexOption.IGNORE_CASE)
    return email.matches(regex)
}

fun isPasswordValid(password: String): Boolean {
    val regex = Regex(Constants.VALID_PASSWORD_REGEX_PATTERN)
    return password.matches(regex)
}