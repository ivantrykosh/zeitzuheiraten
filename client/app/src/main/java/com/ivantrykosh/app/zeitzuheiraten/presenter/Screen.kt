package com.ivantrykosh.app.zeitzuheiraten.presenter

sealed class Screen(val route: String) {
    data object SplashScreen: Screen("splash_screen")
    sealed class AuthScreen(route: String): Screen(route) {
        data object SignUpScreen: AuthScreen("sign_up_screen")
        data object VerifyEmailScreen: AuthScreen("verify_email_screen")
    }
    data object MainScreen: Screen("main_screen")
}