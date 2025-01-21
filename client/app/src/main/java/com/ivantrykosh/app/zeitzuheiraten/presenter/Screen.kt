package com.ivantrykosh.app.zeitzuheiraten.presenter

sealed class Screen(val route: String) {
    data object SplashScreen: Screen("splash_screen")
    sealed class AuthScreen(route: String): Screen(route) {
        data object AuthMainScreen: AuthScreen("auth_main_screen")
        data object SignUpScreen: AuthScreen("sign_up_screen")
        data object SignInScreen: AuthScreen("sign_in_screen")
    }
    sealed class MainScreen(route: String): Screen(route) {
        data object MainScreen: Screen.MainScreen("main_screen")
        data object HomeScreen: Screen.MainScreen("home_screen")
        data object MyProfileScreen: Screen.MainScreen("my_profile_screen")
    }
}