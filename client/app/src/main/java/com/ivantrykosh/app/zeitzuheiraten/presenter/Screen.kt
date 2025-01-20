package com.ivantrykosh.app.zeitzuheiraten.presenter

sealed class Screen(val route: String) {
    data object SplashScreen: Screen("splash_screen")
    sealed class AuthScreen(route: String): Screen(route) {
        data object AuthMainScreen: AuthScreen("auth_main_screen")
        data object SignUpScreen: AuthScreen("sign_up_screen")
        data object SignInScreen: AuthScreen("sign_in_screen")
    }
    data object MainScreen: Screen("main_screen")
}